package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.Cita
import com.nutrizulia.domain.model.CitaConPaciente
import com.nutrizulia.domain.usecase.GetCitaConPacienteUseCase
import com.nutrizulia.domain.usecase.UpdateCita
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarCitaViewModel @Inject constructor(
    private val getCitaConPaciente: GetCitaConPacienteUseCase,
    private val updateCita: UpdateCita
) : ViewModel() {

    private var _citaConPaciente = MutableLiveData<CitaConPaciente>()
    val citaConPaciente: LiveData<CitaConPaciente> get() = _citaConPaciente

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> get() = _errores

    private var _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private var _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun obtenerPaciente(idCita: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = getCitaConPaciente(idCita)
            if (result != null) {
                _citaConPaciente.value = result
                _salir.value = false
            } else {
                _mensaje.value = "Error: Cita no encontrada"
                _salir.value = true
            }
            _isLoading.value = false
        }
    }

    fun actualizarCita(cita: Cita) {
        val erroresMap = validarCita(cita)
        if (erroresMap.isNotEmpty()) {
            _mensaje.value = "Error: Corrige los campos en rojo."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = updateCita(cita)
            if (result > 0) {
                _mensaje.value = "Cita actualizada correctamente"
                _salir.value = true
            } else {
                _mensaje.value = "Error: No se pudo actualizar la cita"
                _salir.value = false
            }
            _isLoading.value = false
        }
    }

    private fun validarCita(cita: Cita): Map<String, String> {
        val erroresActuales = _errores.value?.toMutableMap() ?: mutableMapOf()
        erroresActuales.clear()

        if (cita.tipoCita.isBlank()) erroresActuales["tipoCita"] = "El tipo de cita es obligatorio."


        if (cita.especialidad.isBlank()) {
            erroresActuales["especialidad"] = "La especialidad es obligatoria."
        }

        if (cita.fechaProgramada.isBlank()) {
            erroresActuales["fechaProgramada"] = "La fecha de la cita es obligatoria."
        }

        if (cita.horaProgramada.isBlank()) {
            erroresActuales["horaCita"] = "La hora de la cita es obligatoria."
        }

        _errores.value = erroresActuales
        return erroresActuales
    }

}
