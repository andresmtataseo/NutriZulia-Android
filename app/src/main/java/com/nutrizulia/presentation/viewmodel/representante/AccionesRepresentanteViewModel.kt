package com.nutrizulia.presentation.viewmodel.representante

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.domain.usecase.collection.GetRepresentanteById
import com.nutrizulia.domain.usecase.collection.DeleteRepresentanteUseCase
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import com.nutrizulia.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccionesRepresentanteViewModel @Inject constructor(
    private val getRepresentanteById: GetRepresentanteById,
    private val deleteRepresentanteUseCase: DeleteRepresentanteUseCase,
    private val currentInstitutionId: GetCurrentInstitutionIdUseCase
): ViewModel() {

    private val _representante = MutableLiveData<Representante>()
    val representante: LiveData<Representante> = _representante
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    private val _mensaje = MutableLiveData<Event<String>>()
    val mensaje: LiveData<Event<String>> get() = _mensaje
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _salir = MutableLiveData<Event<Boolean>>()
    val salir: LiveData<Event<Boolean>> get() = _salir

    fun onCreate(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val representanteJob = launch { obtenerRepresentante(id) }
                representanteJob.join()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun obtenerRepresentante(id: String) {
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

            val result = getRepresentanteById(idUsuarioInstitucion.value ?: 0, id)
            if (result != null) {
                _representante.value = result
            } else {
                _mensaje.value = Event("No se encontraron datos.")
                _isLoading.value = false
                _salir.value = Event(true)
                return@launch
            }
            _isLoading.value = false
        }
    }

    fun eliminarRepresentante() {
        val representanteActual = _representante.value
        val institutionId = _idUsuarioInstitucion.value
        
        if (representanteActual == null || institutionId == null) {
            _mensaje.value = Event("Error: No se puede eliminar el representante")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resultado = deleteRepresentanteUseCase(representanteActual.id, institutionId)
                
                if (resultado.exitoso) {
                    _mensaje.value = Event(resultado.mensaje)
                    _salir.value = Event(true)
                } else {
                    _mensaje.value = Event(resultado.mensaje)
                }
            } catch (e: Exception) {
                _mensaje.value = Event("Error inesperado al eliminar el representante: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}