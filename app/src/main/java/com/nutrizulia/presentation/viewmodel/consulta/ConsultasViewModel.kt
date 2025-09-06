package com.nutrizulia.presentation.viewmodel.consulta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.domain.usecase.collection.GetPacientesConCitas
import com.nutrizulia.domain.usecase.collection.GetPacientesConCitasByFiltro
import com.nutrizulia.domain.usecase.collection.GetPacientesConCitasByCompleteFilters
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsultasViewModel @Inject constructor(
    private val getPacientesConCitas: GetPacientesConCitas,
    private val getPacientesConCitasByFiltro: GetPacientesConCitasByFiltro,
    private val getPacientesConCitasByCompleteFilters: GetPacientesConCitasByCompleteFilters,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _pacientesConCitas = MutableLiveData<List<PacienteConCita>>()
    val pacientesConCitas: LiveData<List<PacienteConCita>> get() = _pacientesConCitas

    private val _pacientesConCitasFiltrados = MutableLiveData<List<PacienteConCita>>()
    val pacientesConCitasFiltrados: LiveData<List<PacienteConCita>> get() = _pacientesConCitasFiltrados

    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    // Filtros múltiples
    private val _estadosFiltros = MutableLiveData<MutableSet<String>>(mutableSetOf())
    private val _periodosFiltros = MutableLiveData<MutableSet<String>>(mutableSetOf())
    private val _tiposConsultaFiltros = MutableLiveData<MutableSet<String>>(mutableSetOf())
    
    private val _filtrosActivos = MutableLiveData<Boolean>(false)
    val filtrosActivos: LiveData<Boolean> get() = _filtrosActivos

    fun onCreate() {
        obtenerConsultas()
    }

    fun clearMensaje() {
        _mensaje.value = null
    }

    fun obtenerConsultas() {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
            }

            val result = getPacientesConCitas(idUsuarioInstitucion.value ?: 0)
            if (result.isNotEmpty()) {
                _pacientesConCitas.value = result
            }
            _isLoading.value = false
        }
    }

    fun buscarConsultas(query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            if (query.isBlank()) {
                _filtro.value = ""
                _pacientesConCitasFiltrados.value = emptyList()
                _isLoading.value = false
                return@launch
            }

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
                _isLoading.value = false
                return@launch
            }

            _filtro.value = query
            val result = getPacientesConCitasByFiltro(idUsuarioInstitucion.value ?: 0, filtro.value ?: "")
            if (result.isEmpty()) {
                _pacientesConCitasFiltrados.value = emptyList()
                _mensaje.value = "No se encontraron pacientes con citas."
            } else {
                _pacientesConCitasFiltrados.value = result
            }
            _isLoading.value = false
        }
    }

    fun toggleEstadoFilter(estado: String, isChecked: Boolean) {
        val currentEstados = _estadosFiltros.value ?: mutableSetOf()
        if (isChecked) {
            currentEstados.add(estado)
        } else {
            currentEstados.remove(estado)
        }
        _estadosFiltros.value = currentEstados
        aplicarFiltros()
    }

    fun togglePeriodoFilter(periodo: String, isChecked: Boolean) {
        val currentPeriodos = _periodosFiltros.value ?: mutableSetOf()
        if (isChecked) {
            currentPeriodos.add(periodo)
        } else {
            currentPeriodos.remove(periodo)
        }
        _periodosFiltros.value = currentPeriodos
        aplicarFiltros()
    }

    fun toggleTipoConsultaFilter(tipoConsulta: String, isChecked: Boolean) {
        val currentTipos = _tiposConsultaFiltros.value ?: mutableSetOf()
        if (isChecked) {
            currentTipos.add(tipoConsulta)
        } else {
            currentTipos.remove(tipoConsulta)
        }
        _tiposConsultaFiltros.value = currentTipos
        aplicarFiltros()
    }

    fun limpiarFiltros() {
        _estadosFiltros.value = mutableSetOf()
        _periodosFiltros.value = mutableSetOf()
        _tiposConsultaFiltros.value = mutableSetOf()
        _filtro.value = ""
        _filtrosActivos.value = false
        _pacientesConCitasFiltrados.value = emptyList()
        _mensaje.value = null
    }

    private fun aplicarFiltros() {
        val estados = _estadosFiltros.value ?: mutableSetOf()
        val periodos = _periodosFiltros.value ?: mutableSetOf()
        val tiposConsulta = _tiposConsultaFiltros.value ?: mutableSetOf()
        
        val hayFiltrosActivos = estados.isNotEmpty() || periodos.isNotEmpty() || tiposConsulta.isNotEmpty()
        _filtrosActivos.value = hayFiltrosActivos
        
        if (!hayFiltrosActivos) {
            _pacientesConCitasFiltrados.value = emptyList()
            _mensaje.value = null
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al aplicar filtros. No se ha seleccionado una institución."
                _isLoading.value = false
                return@launch
            }
            
            try {
                // Convertir períodos a fechas
                val (fechaInicio, fechaFin) = convertirPeriodosAFechas(periodos)
                
                // Preparar listas de filtros (null si están vacías)
                val estadosList = if (estados.isNotEmpty()) estados.toList() else null
                val tiposConsultaList = if (tiposConsulta.isNotEmpty()) tiposConsulta.toList() else null
                
                // Aplicar filtros usando el caso de uso completo
                val result = getPacientesConCitasByCompleteFilters(
                    _idUsuarioInstitucion.value ?: 0,
                    estadosList,
                    tiposConsultaList,
                    fechaInicio,
                    fechaFin
                )
                
                _pacientesConCitasFiltrados.value = result
                
                if (result.isEmpty()) {
                    _mensaje.value = "No se encontraron consultas con los filtros aplicados."
                } else {
                    _mensaje.value = null
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al aplicar filtros: ${e.message}"
                _pacientesConCitasFiltrados.value = emptyList()
            }
            
            _isLoading.value = false
        }
    }
    
    private fun convertirPeriodosAFechas(periodos: Set<String>): Pair<String?, String?> {
        if (periodos.isEmpty()) return Pair(null, null)
        
        val hoy = java.time.LocalDate.now()
        var fechaInicio: java.time.LocalDate? = null
        var fechaFin: java.time.LocalDate? = null
        
        periodos.forEach { periodo ->
            when (periodo.lowercase()) {
                "hoy" -> {
                    if (fechaInicio == null || hoy.isBefore(fechaInicio)) fechaInicio = hoy
                    if (fechaFin == null || hoy.isAfter(fechaFin)) fechaFin = hoy
                }
                "esta semana" -> {
                    val inicioSemana = hoy.minusDays(hoy.dayOfWeek.value - 1L)
                    val finSemana = inicioSemana.plusDays(6)
                    if (fechaInicio == null || inicioSemana.isBefore(fechaInicio)) fechaInicio = inicioSemana
                    if (fechaFin == null || finSemana.isAfter(fechaFin)) fechaFin = finSemana
                }
                "este mes" -> {
                    val inicioMes = hoy.withDayOfMonth(1)
                    val finMes = hoy.withDayOfMonth(hoy.lengthOfMonth())
                    if (fechaInicio == null || inicioMes.isBefore(fechaInicio)) fechaInicio = inicioMes
                    if (fechaFin == null || finMes.isAfter(fechaFin)) fechaFin = finMes
                }
                "últimos 7 días" -> {
                    val inicio = hoy.minusDays(6)
                    if (fechaInicio == null || inicio.isBefore(fechaInicio)) fechaInicio = inicio
                    if (fechaFin == null || hoy.isAfter(fechaFin)) fechaFin = hoy
                }
                "últimos 30 días" -> {
                    val inicio = hoy.minusDays(29)
                    if (fechaInicio == null || inicio.isBefore(fechaInicio)) fechaInicio = inicio
                    if (fechaFin == null || hoy.isAfter(fechaFin)) fechaFin = hoy
                }
            }
        }
        
        return Pair(
            fechaInicio?.toString(),
            fechaFin?.toString()
        )
    }

}