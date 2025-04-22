package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.CitaConPaciente
import com.nutrizulia.domain.model.Consulta
import com.nutrizulia.domain.model.SignosVitales
import com.nutrizulia.domain.usecase.GetCitaConPacienteUseCase
import com.nutrizulia.domain.usecase.InsertConsultaUseCase
import com.nutrizulia.domain.usecase.InsertSignosVitalesUseCase
import com.nutrizulia.domain.usecase.UpdateEstadoCitaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrarConsultaViewModel @Inject constructor(
    private val insertConsultaUseCase: InsertConsultaUseCase,
    private val insertSignosVitalesUseCase: InsertSignosVitalesUseCase,
    private val getCitaConPacienteUseCase: GetCitaConPacienteUseCase,
    private val updateEstadoCitaUseCase: UpdateEstadoCitaUseCase
): ViewModel() {

    private val _citaConPaciente = MutableLiveData<CitaConPaciente?>()
    val citaConPaciente: LiveData<CitaConPaciente?> get() = _citaConPaciente

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> get() = _errores

    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

    fun cargarCitaConPaciente(idCita: Int) {
        viewModelScope.launch {
            val encontrado = getCitaConPacienteUseCase(idCita)
            if (encontrado != null) {
                _citaConPaciente.value = encontrado
            } else {
                _mensaje.value = "Error al buscar la cita."
                _salir.value = true
            }
        }

    }

    private fun validarConsulta(consulta: Consulta, signosVitales: SignosVitales): Map<String, String> {
        val erroresActuales = _errores.value?.toMutableMap() ?: mutableMapOf()
        erroresActuales.clear()

        if (consulta.diagnosticoPrincipal.isBlank()) erroresActuales["diagPrincipal"] = "El diagnóstico principal es obligatorio."
        if (signosVitales.peso <= 0) erroresActuales["peso"] = "El peso es obligatorio."
        if (signosVitales.altura <= 0) erroresActuales["altura"] = "La altura es obligatoria."

        _errores.value = erroresActuales
        return erroresActuales
    }

    fun registrarConsulta(consulta: Consulta, signosVitales: SignosVitales) {
        val erroresMap = validarConsulta(consulta, signosVitales)
        if (erroresMap.isNotEmpty()) {
            _mensaje.value = "Error: Corrige los campos en rojo."
            return
        }

        viewModelScope.launch {
            val result = insertConsultaUseCase(consulta)
            if (result > 0) {
                signosVitales.consultaId = result.toInt()
                insertSignosVitalesUseCase(signosVitales)
                if (consulta.citaId != null) updateEstadoCitaUseCase(consulta.citaId, "COMPLETADA")
                _mensaje.value = "La consulta se registro correctamente."
                _salir.value = true
            } else {
                _mensaje.value = "Error al registrar la consulta."
            }
        }
    }

}