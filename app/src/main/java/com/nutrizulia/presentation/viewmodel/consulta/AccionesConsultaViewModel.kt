package com.nutrizulia.presentation.viewmodel.consulta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.domain.usecase.collection.GetPacienteConCitaById
import com.nutrizulia.domain.usecase.collection.SaveConsultaEstadoById
import com.nutrizulia.domain.usecase.collection.ValidateConsultaEditabilityUseCase
import com.nutrizulia.domain.usecase.collection.ValidateConsultaEditabilityUseCase.ValidationResult
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccionesConsultaViewModel @Inject constructor(
    private val getPacienteConCitaById: GetPacienteConCitaById,
    private val sessionManager: SessionManager,
    private val saveConsultaEstadoById: SaveConsultaEstadoById,
    private val validateConsultaEditabilityUseCase: ValidateConsultaEditabilityUseCase
) : ViewModel() {

    private val _pacienteConCita = MutableLiveData<PacienteConCita>()
    val pacienteConCita: LiveData<PacienteConCita> get() = _pacienteConCita
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    private val _mensaje = MutableLiveData<Event<String>>()
    val mensaje: LiveData<Event<String>> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _salir = MutableLiveData<Event<Boolean>>()
    val salir: LiveData<Event<Boolean>> get() = _salir

    private val _canEditConsulta = MutableLiveData<Event<Boolean>>()
    val canEditConsulta: LiveData<Event<Boolean>> get() = _canEditConsulta

    fun onCreate(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pacienteJob = launch { obtenerPacienteConCita(id) }
                pacienteJob.join()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun obtenerPacienteConCita(consultaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = Event("No se ha seleccionado una institución.")
                _isLoading.value = false
                _salir.value = Event(true)
                return@launch
            }

            val result = getPacienteConCitaById(idUsuarioInstitucion.value ?: 0, consultaId)
            if (result != null) {
                _pacienteConCita.value = result
            } else {
                _mensaje.value = Event("No se encontraron datos.")
                _isLoading.value = false
                _salir.value = Event(true)
                return@launch
            }
            _isLoading.value = false
        }
    }

    fun borrarConsulta(idConsulta: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                saveConsultaEstadoById(idConsulta, Estado.CANCELADA)
                _mensaje.value = Event("Cita cancelada con éxito.")
                _salir.value = Event(true)
            } catch (e: Exception) {
                _mensaje.value = Event("Error al cancelar la cita.")
            } finally {
                _isLoading.value = false
            }

        }

    }
    
    fun validateCanEditConsulta(consultaId: String) {
        viewModelScope.launch {
            val usuarioInstitucionId = idUsuarioInstitucion.value ?: 0
            val result = validateConsultaEditabilityUseCase(usuarioInstitucionId, consultaId)
            when (result) {
                is ValidationResult.Editable -> {
                    _canEditConsulta.value = Event(true)
                }
                is ValidationResult.NotEditable -> {
                    _mensaje.value = Event(result.reason)
                    _canEditConsulta.value = Event(false)
                }
                is ValidationResult.Error -> {
                    _mensaje.value = Event(result.message)
                    _canEditConsulta.value = Event(false)
                }
            }
        }
    }
}