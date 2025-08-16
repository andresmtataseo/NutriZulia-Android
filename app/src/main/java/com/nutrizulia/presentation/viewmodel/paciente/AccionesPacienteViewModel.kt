package com.nutrizulia.presentation.viewmodel.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.data.repository.collection.SoftDeleteResult
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccionesPacienteViewModel @Inject constructor(
    private val getPaciente: GetPacienteById,
    private val pacienteRepository: PacienteRepository,
    private val sessionManager: SessionManager,
    ) : ViewModel() {

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

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

    fun obtenerPaciente(id: String) {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
                _isLoading.value = false
                _salir.value = true
            }

            val result = getPaciente(idUsuarioInstitucion.value ?: 0, id)
            if (result != null) {
                _paciente.value = result
            } else {
                _mensaje.value = "No se encontraron datos."
                _isLoading.value = false
                _salir.value = true
                return@launch
            }
            _isLoading.value = false
        }
    }

    /**
     * Elimina un paciente usando soft delete
     * @param pacienteId ID del paciente a eliminar
     */
    fun eliminarPaciente(pacienteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val usuarioInstitucionId = idUsuarioInstitucion.value ?: 0
                val result = pacienteRepository.softDeletePaciente(usuarioInstitucionId, pacienteId)
                
                when (result) {
                    is SoftDeleteResult.Success -> {
                        _mensaje.value = result.message
                        _salir.value = true
                    }
                    is SoftDeleteResult.Error -> {
                        _mensaje.value = result.message
                    }
                }
            } catch (e: Exception) {
                _mensaje.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}