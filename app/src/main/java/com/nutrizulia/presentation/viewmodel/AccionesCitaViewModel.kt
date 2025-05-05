package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.CitaConPaciente
import com.nutrizulia.domain.usecase.GetCitaConPacienteUseCase
import com.nutrizulia.domain.usecase.UpdateEstadoCitaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccionesCitaViewModel @Inject constructor(
    private val getCitaConPaciente: GetCitaConPacienteUseCase,
    private val updateEstadoCitaUseCase: UpdateEstadoCitaUseCase,
) : ViewModel() {

    private var _citaConPaciente = MutableLiveData<CitaConPaciente>()
    val citaConPaciente: LiveData<CitaConPaciente> get() = _citaConPaciente

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
            } else {
                _mensaje.value = "Error: Cita no encontrada"
            }
            _isLoading.value = false
        }
    }

    fun cancelarCita(idCita: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = updateEstadoCitaUseCase(idCita, "CANCELADA")
            if (result > 0) {
                _salir.value = true
                _mensaje.value = "Cita cancelada con éxito"
            } else {
                _mensaje.value = "Error: La cita no pudo ser cancelada"
            }
            _isLoading.value = false
        }

    }
}