package com.nutrizulia.presentation.viewmodel

import android.util.Log
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
            _citaConPaciente.value = encontrado
        }

    }

    private fun validarConsulta(consulta: Consulta, signosVitales: SignosVitales): Map<String, String> {
        val erroresActuales = _errores.value?.toMutableMap() ?: mutableMapOf()
        erroresActuales.clear()

        if (signosVitales.peso <= 0) erroresActuales["peso"] = "El peso es obligatorio."
        if (signosVitales.altura <= 0) erroresActuales["altura"] = "La altura es obligatoria."

        _errores.value = erroresActuales
        return erroresActuales
    }

// Asegúrate de que _mensaje y _salir son MutableLiveData en tu ViewModel
// Asegúrate de que validarConsulta, insertConsultaUseCase, insertSignosVitalesUseCase,
// y updateEstadoCitaUseCase están definidos/inyectados y son suspend functions.
// MUY IMPORTANTE: insertSignosVitalesUseCase DEBE devolver Long (el ID insertado).

    fun registrarConsulta(consulta: Consulta, signosVitales: SignosVitales) {
        val erroresMap = validarConsulta(consulta, signosVitales)
        if (erroresMap.isNotEmpty()) {
            // Asumiendo que 'errores' es otro MutableLiveData en tu ViewModel
            // errores.value = erroresMap // Si quieres mostrar errores específicos en campos
            _mensaje.value = "Error de validación: Corrige los campos con errores."
            // Puedes añadir lógica aquí para mostrar los errores específicos en la UI si usas 'errores'
            return
        }

        viewModelScope.launch {
            try {
                // --- Paso 1: Registrar la Consulta ---
                val consultaId = insertConsultaUseCase(consulta)

                if (consultaId > 0) {
                    // Consulta registrada exitosamente, ahora registrar signos vitales

                    // Asigna el ID de la consulta recién creada a los signos vitales
                    signosVitales.consultaId = consultaId.toInt()

                    // --- Paso 2: Registrar los Signos Vitales ---
                    // *** NECESITA DEVOLVER LONG ***
                    val signosVitalesId = insertSignosVitalesUseCase(signosVitales)

                    if (signosVitalesId > 0) {
                        // Signos Vitales registrados exitosamente

                        // --- Paso 3: Actualizar estado de la cita (si aplica) ---
                        if (consulta.citaId != null) {
                            // updateEstadoCitaUseCase también debería ser suspend fun y manejar errores si es necesario
                            // Si updateEstadoCitaUseCase puede fallar, también podrías verificar su resultado o atrapar sus excepciones específicas
                            updateEstadoCitaUseCase(consulta.citaId, "COMPLETADA")
                            // Podrías verificar el resultado de la actualización si la función lo devuelve
                        }

                        // --- ÉXITO TOTAL ---
                        _mensaje.postValue("La consulta y los signos vitales se registraron correctamente.")
                        _salir.postValue(true)

                    } else {
                        // Falló la inserción de los Signos Vitales (pero la consulta sí se insertó)
                        // Considera cómo manejar esto: ¿borras la consulta o dejas que quede incompleta?
                        // Por ahora, mostramos un error.
                        _mensaje.postValue("Error: Se registró la consulta pero falló el registro de los signos vitales.")
                        _salir.postValue(false) // No salimos
                        // Opcional: Añadir lógica para borrar la consulta recién insertada si fallan los signos vitales
                        // deleteConsultaUseCase(consultaId) // Necesitarías un Use Case para esto
                    }

                } else {
                    // Falló la inserción de la Consulta
                    _mensaje.postValue("Error al registrar la consulta (no se obtuvo ID válido).")
                    _salir.postValue(false) // No salimos
                }

            } catch (e: Exception) {
                // --- Manejo de CUALQUIER EXCEPCIÓN que ocurra ---
                // Esto atrapará errores de base de datos, de red (si los use cases acceden a la red), etc.
                _mensaje.postValue("Ocurrió un error inesperado durante el registro: ${e.message}")
                _salir.postValue(false) // No salimos

                // MUY importante loguear el error completo para debugging
                Log.e("ViewModel", "Error en registrarConsulta", e)

                // Si usas excepciones personalizadas (como DuplicateCédulaException),
                // podrías tener catch específicos antes de este catch(e: Exception) genérico.
            }
        }
    }

}