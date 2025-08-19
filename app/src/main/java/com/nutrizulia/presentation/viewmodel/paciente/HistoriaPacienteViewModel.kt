package com.nutrizulia.presentation.viewmodel.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PacienteConConsultaYDetalles
import com.nutrizulia.domain.usecase.collection.GetConsultasByPacienteId
import com.nutrizulia.domain.usecase.collection.GetConsultasByPacienteIdAndFiltro
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoriaPacienteViewModel @Inject constructor(
    private val getConsultasDetalladasByPacienteId: GetConsultasByPacienteId,
    private val getConsultasDetalladasByPacienteIdAndFiltro: GetConsultasByPacienteIdAndFiltro,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _consultasDetalladas = MutableLiveData<List<PacienteConConsultaYDetalles>>()
    val consultasDetalladas: LiveData<List<PacienteConConsultaYDetalles>> get() = _consultasDetalladas

    private val _consultasDetalladasFiltradas = MutableLiveData<List<PacienteConConsultaYDetalles>>()
    val pacientesConCitasFiltrados: LiveData<List<PacienteConConsultaYDetalles>> get() = _consultasDetalladasFiltradas

    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    fun onCreate(pacienteId: String) {
        obtenerConsultas(pacienteId)
    }

    fun clearMensaje() {
        _mensaje.value = null
    }

    fun obtenerConsultas(pacienteId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar consultas. No se ha seleccionado una institución."
            }

            val result = getConsultasDetalladasByPacienteId(pacienteId)
            if (result.isNotEmpty()) {
                _consultasDetalladas.value = result
            }
            _isLoading.value = false
        }
    }

    fun buscarConsultas(pacienteId: String, query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            if (query.isBlank()) {
                _filtro.value = ""
                _consultasDetalladasFiltradas.value = emptyList()
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
            val result = getConsultasDetalladasByPacienteIdAndFiltro(pacienteId, filtro.value ?: "")
            if (result.isEmpty()) {
                _consultasDetalladasFiltradas.value = emptyList()
                _mensaje.value = "No se encontraron las consultas."
            } else {
                _consultasDetalladasFiltradas.value = result
            }
            _isLoading.value = false
        }
    }

}