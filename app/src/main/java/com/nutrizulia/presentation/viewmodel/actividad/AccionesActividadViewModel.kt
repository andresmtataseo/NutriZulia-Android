package com.nutrizulia.presentation.viewmodel.actividad

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.domain.usecase.collection.GetActividadConTipoById
import com.nutrizulia.domain.usecase.collection.DeleteActividadUseCase
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import com.nutrizulia.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccionesActividadViewModel @Inject constructor(
    private val getActividadConTipoById: GetActividadConTipoById,
    private val deleteActividadUseCase: DeleteActividadUseCase,
    private val currentInstitutionId: GetCurrentInstitutionIdUseCase
): ViewModel() {

    private val _actividad = MutableLiveData<ActividadConTipo>()
    val actividad: LiveData<ActividadConTipo> = _actividad
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

    // Agregar canEditActividad como evento de un solo uso
    private val _canEditActividad = MutableLiveData<Event<Boolean>>()
    val canEditActividad: LiveData<Event<Boolean>> get() = _canEditActividad

    // Agregar deletionResult como evento de un solo uso
    private val _deletionResult = MutableLiveData<Event<Boolean>>()
    val deletionResult: LiveData<Event<Boolean>> get() = _deletionResult

    fun onCreate(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val actividadJob = launch { obtenerActividad(id) }
                actividadJob.join()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun validateCanEditActividad(actividadId: String) {
        viewModelScope.launch {
            try {
                val actividad = _actividad.value
                if (actividad == null) {
                    _mensaje.value = Event("No se encontró la actividad")
                    _canEditActividad.value = Event(false)
                    return@launch
                }
                
                // Validar si la actividad puede ser editada (no debe estar sincronizada)
                if (actividad.isSynced) {
                    _mensaje.value = Event("No se puede editar la actividad porque ya ha sido sincronizada.")
                    _canEditActividad.value = Event(false)
                } else {
                    _canEditActividad.value = Event(true)
                }
            } catch (e: Exception) {
                _mensaje.value = Event("Error al validar la editabilidad: ${e.message}")
                _canEditActividad.value = Event(false)
            }
        }
    }

    fun deleteActividadPermanently(actividadId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val usuarioInstitucionId = idUsuarioInstitucion.value ?: 0
            val result = deleteActividadUseCase(actividadId, usuarioInstitucionId)
            
            _mensaje.value = Event(result.message)
            _deletionResult.value = Event(result.success)
            _isLoading.value = false
        }
    }

    fun obtenerActividad(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val institutionId = currentInstitutionId() ?: throw IllegalStateException(
                    "No se ha seleccionado una institución."
                )
                _idUsuarioInstitucion.value = institutionId
            } catch (e: Exception) {
                _mensaje.value = Event("No se ha seleccionado una institución.")
                _isLoading.value = false
                _salir.value = Event(true)
                return@launch
            }

            val result = getActividadConTipoById(id, idUsuarioInstitucion.value ?: 0)
            if (result != null) {
                _actividad.value = result
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