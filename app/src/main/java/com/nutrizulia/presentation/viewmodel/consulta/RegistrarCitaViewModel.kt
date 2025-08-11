package com.nutrizulia.presentation.viewmodel.consulta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.domain.model.catalog.Especialidad
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.catalog.GetEspecialidadById
import com.nutrizulia.domain.usecase.catalog.GetEspecialidades
import com.nutrizulia.domain.usecase.catalog.GetTipoActividadById
import com.nutrizulia.domain.usecase.catalog.GetTiposActividades
import com.nutrizulia.domain.usecase.collection.DetermineTipoConsulta
import com.nutrizulia.domain.usecase.collection.GetAppointmentCounts
import com.nutrizulia.domain.usecase.collection.GetConsultaProgramadaById
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.domain.usecase.collection.SaveConsulta
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import com.nutrizulia.domain.usecase.user.GetMaxAppointmentsPerDayValueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class RegistrarCitaViewModel @Inject constructor(
    private val saveConsulta: SaveConsulta,
    private val getPaciente: GetPacienteById,
    private val getConsulta: GetConsultaProgramadaById,
    private val getTipoActividad: GetTipoActividadById,
    private val getEspecialidad: GetEspecialidadById,
    private val getTiposActividades: GetTiposActividades,
    private val getEspecialidades: GetEspecialidades,
    private val determineTipoConsulta: DetermineTipoConsulta,
    private val getAppointmentCounts: GetAppointmentCounts,
    private val getCurrentInstitutionId: GetCurrentInstitutionIdUseCase,
    private val getMaxAppointmentsPerDayValue: GetMaxAppointmentsPerDayValueUseCase
) : ViewModel() {

    // --- State & UI Events LiveData ---
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> = _salir

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> = _errores

    // --- Data LiveData ---
    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente

    private val _consulta = MutableLiveData<Consulta>()
    val consulta: LiveData<Consulta> = _consulta

    private val _tipoActividad = MutableLiveData<TipoActividad>()
    val tipoActividad: LiveData<TipoActividad> = _tipoActividad

    private val _especialidad = MutableLiveData<Especialidad>()
    val especialidad: LiveData<Especialidad> = _especialidad

    private val _tipoConsulta = MutableLiveData<TipoConsulta>()
    val tipoConsulta: LiveData<TipoConsulta> = _tipoConsulta

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    // --- Catalog LiveData ---
    private val _tiposActividades = MutableLiveData<List<TipoActividad>>()
    val tiposActividades: LiveData<List<TipoActividad>> = _tiposActividades

    private val _especialidades = MutableLiveData<List<Especialidad>>()
    val especialidades: LiveData<List<Especialidad>> = _especialidades

    private val _fullyBookedDates = MutableLiveData<List<LocalDate>>()
    val fullyBookedDates: LiveData<List<LocalDate>> = _fullyBookedDates

    /**
     * Inicia la carga de todos los datos necesarios para la pantalla.
     * Carga el paciente, los catálogos y la información de la cita existente en paralelo.
     */
    fun onCreate(idPaciente: String, idConsulta: String?, isEditable: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Obtener el ID de la institución, es requerido para las demás llamadas.
                val institutionId = getCurrentInstitutionId() ?: throw IllegalStateException(
                    "No se ha seleccionado una institución."
                )
                _idUsuarioInstitucion.value = institutionId

                // 2. Cargar datos en paralelo para mejorar el rendimiento.
                coroutineScope {
                    val pacienteJob = async { executeGetPaciente(idPaciente, institutionId) }
                    val catalogosJob = async { executeLoadCatalogs() }
                    val countsJob = async { executeGetAppointmentCounts(institutionId) }

                    // Esperar a que las tareas críticas terminen
                    pacienteJob.await()
                    catalogosJob.await()
                    countsJob.await()
                }

                // 3. Cargar la consulta existente o determinar el tipo de la nueva consulta.
                if (idConsulta != null) {
                    executeGetConsulta(idConsulta)
                } else {
                    executeDetermineTipoConsulta(idPaciente)
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar datos: ${e.localizedMessage}"
                _salir.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Valida y guarda la información de la cita.
     */
    fun guardarConsulta(consulta: Consulta) {
        if (validarConsulta(consulta).isNotEmpty()) {
            _mensaje.value = "Por favor, corrige los campos marcados en rojo."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val institutionId = _idUsuarioInstitucion.value
                    ?: throw IllegalStateException("El ID de la institución no puede ser nulo.")

                consulta.usuarioInstitucionId = institutionId
                saveConsulta(consulta)

                _mensaje.value = "Cita guardada correctamente."
                _salir.value = true
            } catch (e: Exception) {
                _mensaje.value = "Error inesperado al guardar la cita: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- Private Suspend Functions for Data Loading ---

    private suspend fun executeGetPaciente(idPaciente: String, institutionId: Int) {
        _paciente.value = getPaciente(institutionId, idPaciente)
            ?: throw IllegalStateException("No se encontró el paciente.")
    }

    private suspend fun executeGetConsulta(idConsulta: String) {
        val consultaResult = getConsulta(idConsulta)
            ?: throw IllegalStateException("No se encontró la consulta.")
        _consulta.value = consultaResult

        // Cargar detalles de la consulta en paralelo
        coroutineScope {
            async { _tipoActividad.value = getTipoActividad(consultaResult.tipoActividadId) }
            async { _especialidad.value = getEspecialidad(consultaResult.especialidadRemitenteId) }
        }
        _tipoConsulta.value =
            TipoConsulta.valueOf(consultaResult.tipoConsulta?.name ?: TipoConsulta.CONSULTA_SUCESIVA.name)
    }

    private suspend fun executeDetermineTipoConsulta(pacienteId: String) {
        _tipoConsulta.value = determineTipoConsulta(pacienteId)
    }

    private suspend fun executeLoadCatalogs() {
        coroutineScope {
            async { _tiposActividades.value = getTiposActividades() }
            async { _especialidades.value = getEspecialidades() }
        }
    }

    private suspend fun executeGetAppointmentCounts(institutionId: Int) {
        val counts = getAppointmentCounts(institutionId)
        val maxAppointments = getMaxAppointmentsPerDayValue()
        _fullyBookedDates.value = counts.filter { (_, count) ->
            count >= maxAppointments
        }.map { it.key }
    }

    // --- Validation Logic ---

    private fun validarConsulta(consulta: Consulta): Map<String, String> {
        val erroresActuales = mutableMapOf<String, String>()

        if (consulta.tipoActividadId <= 0) {
            erroresActuales["tipoActividad"] = "El tipo de actividad es obligatorio."
        }
        if (consulta.especialidadRemitenteId <= 0) {
            erroresActuales["especialidad"] = "La especialidad es obligatoria."
        }
        if (consulta.tipoConsulta == null) {
            erroresActuales["tipoConsulta"] = "El tipo de consulta es obligatorio."
        }
        if (consulta.fechaHoraProgramada == null) {
            erroresActuales["fechaProgramada"] = "La fecha y hora son obligatorias."
        } else {
            val fechaCita = consulta.fechaHoraProgramada.toLocalDate()
            val horaCita = consulta.fechaHoraProgramada.toLocalTime()
            val hoy = LocalDate.now()

            if (fechaCita.isBefore(hoy)) {
                erroresActuales["fechaProgramada"] = "La fecha no puede ser anterior al día de hoy."
            } else if (fechaCita.isEqual(hoy) && horaCita.isBefore(LocalTime.now())) {
                erroresActuales["horaProgramada"] = "La hora no puede ser anterior a la actual."
            }
        }

        _errores.value = erroresActuales
        return erroresActuales
    }
}