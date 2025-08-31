package com.nutrizulia.presentation.viewmodel.actividad

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.domain.usecase.collection.GetActividadConTipoById
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccionesActividadViewModel @Inject constructor(
    private val getActividadConTipoById: GetActividadConTipoById,
    private val currentInstitutionId: GetCurrentInstitutionIdUseCase
): ViewModel() {

    private val _actividad = MutableLiveData<ActividadConTipo>()
    val actividad: LiveData<ActividadConTipo> = _actividad
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
                val actividadJob = launch { obtenerActividad(id) }
                actividadJob.join()
            } finally {
                _isLoading.value = false
            }
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
                _mensaje.value = "No se ha seleccionado una institución."
                _isLoading.value = false
                _salir.value = true
                return@launch
            }

            val result = getActividadConTipoById(id, idUsuarioInstitucion.value ?: 0)
            if (result != null) {
                _actividad.value = result
            } else {
                _mensaje.value = "No se encontraron datos."
                _isLoading.value = false
                _salir.value = true
                return@launch
            }
            _isLoading.value = false
        }
    }

}