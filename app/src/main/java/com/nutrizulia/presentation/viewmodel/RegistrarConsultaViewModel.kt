package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.domain.model.catalog.Enfermedad
import com.nutrizulia.domain.model.catalog.Especialidad
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import com.nutrizulia.domain.model.collection.DetallePediatrico
import com.nutrizulia.domain.model.collection.DetalleVital
import com.nutrizulia.domain.model.collection.Diagnostico
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.catalog.GetEnfermedades
import com.nutrizulia.domain.usecase.catalog.GetEspecialidadById
import com.nutrizulia.domain.usecase.catalog.GetEspecialidades
import com.nutrizulia.domain.usecase.catalog.GetRiesgosBiologicos
import com.nutrizulia.domain.usecase.catalog.GetTipoActividadById
import com.nutrizulia.domain.usecase.catalog.GetTiposActividades
import com.nutrizulia.domain.usecase.collection.GetConsultaProgramadaById
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.domain.usecase.collection.SaveConsulta
import com.nutrizulia.domain.usecase.collection.SaveDetalleAntropometrico
import com.nutrizulia.domain.usecase.collection.SaveDetalleMetabolico
import com.nutrizulia.domain.usecase.collection.SaveDetalleObstetricia
import com.nutrizulia.domain.usecase.collection.SaveDetallePediatrico
import com.nutrizulia.domain.usecase.collection.SaveDetalleVital
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrarConsultaViewModel @Inject constructor(
    private val saveConsulta: SaveConsulta,
    private val saveDetalleVital: SaveDetalleVital,
    private val saveDetalleAntropometrico: SaveDetalleAntropometrico,
    private val saveDetalleMetabolico: SaveDetalleMetabolico,
    private val saveDetallePediatrico: SaveDetallePediatrico,
    private val saveDetalleObstetricia: SaveDetalleObstetricia,
    private val getPaciente: GetPacienteById,
    private val getConsulta: GetConsultaProgramadaById,

    private val getTipoActividad: GetTipoActividadById,
    private val getEspecialidad: GetEspecialidadById,

    private val getTiposActividades: GetTiposActividades,
    private val getEspecialidades: GetEspecialidades,
    private val getRiegosBiologicos: GetRiesgosBiologicos,
    private val getEndermedades: GetEnfermedades,

    private val sessionManager: SessionManager
): ViewModel() {

    private val _consulta = MutableLiveData<Consulta>()
    val consulta: LiveData<Consulta> = _consulta
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> = _idUsuarioInstitucion
    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente

    // Datos Individuales
    private val _tipoActividad = MutableLiveData<TipoActividad>()
    val tipoActividad: LiveData<TipoActividad> = _tipoActividad
    private val _especialidad = MutableLiveData<Especialidad>()
    val especialidad: LiveData<Especialidad> = _especialidad
    private val _tipoConsulta = MutableLiveData<TipoConsulta>()
    val tipoConsulta: LiveData<TipoConsulta> = _tipoConsulta

    private val _diagnostico = MutableLiveData<Diagnostico>()
    val diagnostico: LiveData<Diagnostico> = _diagnostico
    private val _riesgoBiologico = MutableLiveData<RiesgoBiologico>()
    val riesgoBiologico: LiveData<RiesgoBiologico> = _riesgoBiologico
    private val _enfermedad = MutableLiveData<Enfermedad>()
    val enfermedad: LiveData<Enfermedad> = _enfermedad

    private  val _detalleVital = MutableLiveData<DetalleVital>()
    val detalleVital: LiveData<DetalleVital> = _detalleVital
    private val _detalleMetabolico = MutableLiveData<DetalleMetabolico>()
    val detalleMetabolico: LiveData<DetalleMetabolico> = _detalleMetabolico
    private val _detalleAntropometrico = MutableLiveData<DetalleAntropometrico>()
    val detalleAntropometrico: LiveData<DetalleAntropometrico> = _detalleAntropometrico
    private val _detallePediatrico = MutableLiveData<DetallePediatrico>()
    val detallePediatrico: LiveData<DetallePediatrico> = _detallePediatrico
    private val _detalleObstetricia = MutableLiveData<DetalleObstetricia>()
    val detalleObstetricia: LiveData<DetalleObstetricia> = _detalleObstetricia

    // Datos colectivos
    private val _tiposActividades = MutableLiveData<List<TipoActividad>>()
    val tiposActividades: LiveData<List<TipoActividad>> = _tiposActividades
    private val _especialidades = MutableLiveData<List<Especialidad>>()
    val especialidades: LiveData<List<Especialidad>> = _especialidades
    private val _tiposConsultas = MutableLiveData<List<TipoConsulta>>()
    val tiposConsultas: LiveData<List<TipoConsulta>> = _tiposConsultas

    private val _diagnosticos = MutableLiveData<List<Diagnostico>>()
    val diagnosticos: LiveData<List<Diagnostico>> = _diagnosticos
    private val _riesgosBiologicos = MutableLiveData<List<RiesgoBiologico>>()
    val riesgosBiologicos: LiveData<List<RiesgoBiologico>> = _riesgosBiologicos
    private val _enfermedades = MutableLiveData<List<Enfermedad>>()
    val enfermedades: LiveData<List<Enfermedad>> = _enfermedades

    // Datos informativos
    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> = _errores
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> = _salir

    fun onCreate(idPaciente: String, idConsulta: String?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val idUsuarioInstitucion = obtenerIdUsuarioInstitucion()
                val pacienteDeferred = async { getPaciente(idUsuarioInstitucion, idPaciente) }

                if (idConsulta != null) {
                    val consultaExistente = getConsulta(idConsulta)
                        ?: throw IllegalStateException("La consulta a editar no fue encontrada.")
                    _consulta.postValue(consultaExistente)
                    async { _tipoActividad.postValue(getTipoActividad(consultaExistente.tipoActividadId)) }
                    async { _especialidad.postValue(getEspecialidad(consultaExistente.especialidadRemitenteId)) }
                } else {
                    async { _tiposActividades.postValue(getTiposActividades()) }
                    async { _especialidades.postValue(getEspecialidades()) }
                    async { _tiposConsultas.postValue(TipoConsulta.entries) }
                }

                val pacienteCargado = pacienteDeferred.await()
                    ?: throw IllegalStateException("El paciente no fue encontrado.")
                _paciente.postValue(pacienteCargado)

            } catch (e: Exception) {
                _mensaje.postValue(e.message ?: "Error desconocido al cargar datos.")
                _salir.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private suspend fun obtenerIdUsuarioInstitucion(): Int {
        return sessionManager.currentInstitutionIdFlow.firstOrNull()
            ?: throw IllegalStateException("ID de institución no encontrado en la sesión. No se puede continuar.")
    }

    private fun obtenerPaciente(idPaciente: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
                _isLoading.value = false
                _salir.value = true
            }

            val paciente = getPaciente(idUsuarioInstitucion.value ?: 0 ,idPaciente)
            if (paciente == null) {
                _mensaje.postValue("No se encontró el paciente")
                _salir.postValue(true)
                return@launch
            }
            _paciente.value = paciente
            _isLoading.postValue(false)
        }
    }

    private fun obtenerConsulta(idConsulta: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val consulta = getConsulta(idConsulta)
            if (consulta == null) {
                _mensaje.postValue("No se encontró la consulta")
                _salir.postValue(true)
                return@launch
            }
            _consulta.value = consulta
            _tipoActividad.value = getTipoActividad(consulta.tipoActividadId)
            _especialidad.value = getEspecialidad(consulta.especialidadRemitenteId)
            _tipoConsulta.value = TipoConsulta.valueOf(consulta.tipoConsulta?.name ?: TipoConsulta.CONSULTA_SUCESIVA.name)
            _isLoading.postValue(false)
        }
    }

    private fun cargarCatalogosCita() {
        cargarTiposActividades()
        cargarEspecialidades()
        cargarTiposConsultas()
    }

    private fun cargarTiposActividades() {
        viewModelScope.launch {
            val lista = getTiposActividades()
            _tiposActividades.value = lista
        }
    }

    private fun cargarEspecialidades() {
        viewModelScope.launch {
            val lista = getEspecialidades()
            _especialidades.value = lista
        }
    }

    private fun cargarTiposConsultas() {
        _tiposConsultas.value = TipoConsulta.entries
    }

    private fun validarConsulta(consulta: Consulta): Map<String, String> {
        val erroresActuales = _errores.value?.toMutableMap() ?: mutableMapOf()
        erroresActuales.clear()

        if (consulta.tipoActividadId <= 0) {
            erroresActuales["tipoActividad"] = "El tipo de actividad es obligatorio."
        }
        if (consulta.especialidadRemitenteId <= 0) {
            erroresActuales["especialidad"] = "La especialidad es obligatoria."
        }
        if (consulta.tipoConsulta == null) {
            erroresActuales["tipoConsulta"] = "El tipo de consulta es obligatorio."
        }

        _errores.value = erroresActuales
        return erroresActuales
    }


    fun guardarConsulta(
        consulta: Consulta,
        detalleVital: DetalleVital,
        detalleMetabolico: DetalleMetabolico,
        detalleAntropometrico: DetalleAntropometrico,
        detallePediatrico: DetallePediatrico?,
        detalleObstetricia: DetalleObstetricia?
    ) {
        if (validarConsulta(consulta).isNotEmpty()) {
            _mensaje.value = "Por favor, corrige los campos obligatorios."
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val idUsuarioInstitucion = obtenerIdUsuarioInstitucion()
                val consultaConId = consulta.copy(usuarioInstitucionId = idUsuarioInstitucion)

                coroutineScope {
                    saveConsulta(consultaConId)
                    launch { saveDetalleVital(detalleVital) }
                    launch { saveDetalleMetabolico(detalleMetabolico) }
                    launch { saveDetalleAntropometrico(detalleAntropometrico) }
                    detallePediatrico?.let { launch { saveDetallePediatrico(it) } }
                    detalleObstetricia?.let { launch { saveDetalleObstetricia(it) } }
                }

                _mensaje.postValue("Consulta guardada con éxito.")
                _salir.postValue(true)

            } catch (e: Exception) {
                _mensaje.postValue(e.message ?: "Ocurrió un error inesperado al guardar.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

}