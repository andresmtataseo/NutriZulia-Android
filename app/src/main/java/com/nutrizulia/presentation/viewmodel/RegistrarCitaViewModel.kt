package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.Cita
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.domain.usecase.GetPacienteByIdUseCase
import com.nutrizulia.domain.usecase.InsertCitaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrarCitaViewModel @Inject constructor(
    private val insertCitaUseCase: InsertCitaUseCase,
    private val getPacienteByIdUseCase: GetPacienteByIdUseCase
) : ViewModel() {

    private val _paciente = MutableLiveData<Paciente?>()
    val paciente: LiveData<Paciente?> get() = _paciente

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> get() = _errores

    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

    fun cargarPaciente(idPaciente: Int) {
        viewModelScope.launch {
            val encontrado = getPacienteByIdUseCase(idPaciente)
            if (encontrado != null) {
                _paciente.value = encontrado
            } else {
                _mensaje.value = "Error al buscar el paciente."
                _salir.value = true
            }
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

    fun registrarCita(cita: Cita) {
        val erroresMap = validarCita(cita)
        if (erroresMap.isNotEmpty()) {
            _mensaje.value = "Error: Corrige los campos en rojo."
            return
        }

        viewModelScope.launch {
            val result = insertCitaUseCase(cita)
            if (result > 0) {
                _mensaje.value = "La cita se registro correctamente."
                _salir.value = true
            } else {
                _mensaje.value = "Error al registrar la cita."
            }
        }
    }

}