package com.nutrizulia.presentation.viewmodel.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.domain.usecase.collection.ValidatePacienteCanBeEditedUseCase
import com.nutrizulia.domain.usecase.collection.DeletePacienteUseCase
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

// Clase para eventos de un solo uso
class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

@HiltViewModel
class AccionesPacienteViewModel @Inject constructor(
    private val getPaciente: GetPacienteById,
    private val validatePacienteCanBeEdited: ValidatePacienteCanBeEditedUseCase,
    private val deletePaciente: DeletePacienteUseCase,
    private val sessionManager: SessionManager,
    ) : ViewModel() {

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    // Cambiar mensaje a evento de un solo uso
    private val _mensaje = MutableLiveData<Event<String>>()
    val mensaje: LiveData<Event<String>> get() = _mensaje
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    // Cambiar salir a evento de un solo uso
    private val _salir = MutableLiveData<Event<Boolean>>()
    val salir: LiveData<Event<Boolean>> get() = _salir

    // Cambiar canEditPaciente a evento de un solo uso
    private val _canEditPaciente = MutableLiveData<Event<Boolean>>()
    val canEditPaciente: LiveData<Event<Boolean>> get() = _canEditPaciente

    // Cambiar deletionResult a evento de un solo uso
    private val _deletionResult = MutableLiveData<Event<Boolean>>()
    val deletionResult: LiveData<Event<Boolean>> get() = _deletionResult

    fun validateCanEditPaciente(pacienteId: String) {
        viewModelScope.launch {
            val result = validatePacienteCanBeEdited(pacienteId)
            if (!result.puedeEditar) {
                _mensaje.value = Event(result.mensaje)
                _canEditPaciente.value = Event(false)
            } else {
                _canEditPaciente.value = Event(true)
            }
        }
    }

    fun onCreate(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pacienteJob = launch { obtenerPaciente(id) }
                pacienteJob.join()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePacientePermanently(pacienteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val usuarioInstitucionId = idUsuarioInstitucion.value ?: 0
            val result = deletePaciente(pacienteId, usuarioInstitucionId)
            
            _mensaje.value = Event(result.mensaje)
            _deletionResult.value = Event(result.exitoso)
            _isLoading.value = false
        }
    }

    fun obtenerPaciente(id: String) {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = Event("Error al buscar pacientes. No se ha seleccionado una institución.")
                _isLoading.value = false
                _salir.value = Event(true)
            }

            val result = getPaciente(idUsuarioInstitucion.value ?: 0, id)
            if (result != null) {
                _paciente.value = result
            } else {
                _mensaje.value = Event("No se encontraron datos.")
                _isLoading.value = false
                _salir.value = Event(true)
                return@launch
            }
            _isLoading.value = false
        }
    }
}