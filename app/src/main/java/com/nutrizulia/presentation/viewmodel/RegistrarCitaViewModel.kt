package com.nutrizulia.presentation.viewmodel

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
import com.nutrizulia.domain.usecase.collection.GetConsultaProgramadaById
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.domain.usecase.collection.SaveConsulta
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
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
    private val sessionManager: SessionManager,
    private val getTiposActividades: GetTiposActividades,
    private val getEspecialidades: GetEspecialidades,
    private val determineTipoConsulta: DetermineTipoConsulta
) : ViewModel() {

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente
    private val _consulta = MutableLiveData<Consulta>()
    val consulta: LiveData<Consulta> = _consulta
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    private val _tipoActividad = MutableLiveData<TipoActividad>()
    val tipoActividad: LiveData<TipoActividad> = _tipoActividad
    private val _especialidad = MutableLiveData<Especialidad>()
    val especialidad: LiveData<Especialidad> = _especialidad
    private val _tipoConsulta = MutableLiveData<TipoConsulta>()
    val tipoConsulta: LiveData<TipoConsulta> = _tipoConsulta

    private val _tiposActividades = MutableLiveData<List<TipoActividad>>()
    val tiposActividades: LiveData<List<TipoActividad>> = _tiposActividades
    private val _especialidades = MutableLiveData<List<Especialidad>>()
    val especialidades: LiveData<List<Especialidad>> = _especialidades

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> = _errores
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> = _salir

    fun onCreate(idPaciente: String, idConsulta: String?, isEditable: Boolean) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pacienteJob = launch { obtenerPaciente(idPaciente) }
                val catalogosJob = launch { cargarCatalogos() }

                pacienteJob.join()
                catalogosJob.join()

                if (idConsulta != null) {
                    obtenerConsulta(idConsulta)
                } else {
                    executeDetermineTipoConsulta(idPaciente)
                }

            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun executeDetermineTipoConsulta(pacienteId: String) {
        viewModelScope.launch {
            val tipo: TipoConsulta = determineTipoConsulta(pacienteId)
            _tipoConsulta.postValue(tipo)
        }
    }

    private fun obtenerPaciente(idPaciente: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
                _isLoading.value = false
                _salir.value = true
            }

            val paciente = getPaciente(idUsuarioInstitucion.value ?: 0 ,idPaciente)
            if (paciente == null) {
                _mensaje.postValue("No se encontró el paciente")
                _salir.postValue(true)
                return@launch
            }
            _paciente.value = paciente
            _isLoading.postValue(false)
        }
    }

    private fun obtenerConsulta(idConsulta: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val consulta = getConsulta(idConsulta)
            if (consulta == null) {
                _mensaje.postValue("No se encontró la consulta")
                _salir.postValue(true)
                return@launch
            }
            _consulta.value = consulta
            _tipoActividad.value = getTipoActividad(consulta.tipoActividadId)
            _especialidad.value = getEspecialidad(consulta.especialidadRemitenteId)
            _tipoConsulta.value = TipoConsulta.valueOf(consulta.tipoConsulta?.name ?: TipoConsulta.CONSULTA_SUCESIVA.name)
            _isLoading.postValue(false)
        }
    }

    private fun cargarCatalogos() {
        cargarTiposActividades()
        cargarEspecialidades()
    }

    private fun cargarTiposActividades() {
        viewModelScope.launch {
            val lista = getTiposActividades()
            _tiposActividades.value = lista
        }
    }

    private fun cargarEspecialidades() {
        viewModelScope.launch {
            val lista = getEspecialidades()
            _especialidades.value = lista
        }
    }

    private fun validarConsulta(consulta: Consulta): Map<String, String> {
        val erroresActuales = _errores.value?.toMutableMap() ?: mutableMapOf()
        erroresActuales.clear()

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
            erroresActuales["fechaProgramada"] = "El formato de fecha u hora no es válido."
        } else {
            val fechaCita = consulta.fechaHoraProgramada.toLocalDate()
            val horaCita = consulta.fechaHoraProgramada.toLocalTime()

            val hoy = LocalDate.now()
            val horaActual = LocalTime.now()

            if (fechaCita.isBefore(hoy)) {
                erroresActuales["fechaProgramada"] = "La fecha no puede ser anterior al día de hoy."

            } else if (fechaCita.isEqual(hoy) && horaCita.isBefore(horaActual)) {
                erroresActuales["horaProgramada"] = "La hora no puede ser anterior a la actual."
            }
        }

        _errores.value = erroresActuales
        return erroresActuales
    }

    fun guardarConsulta(consulta: Consulta) {
        val erroresMap = validarConsulta(consulta)
        if (erroresMap.isNotEmpty()) {
            _mensaje.value = "Corrige los campos en rojo."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val institutionId = sessionManager.currentInstitutionIdFlow.firstOrNull()
                    ?: throw IllegalStateException("El ID de la institución no puede ser nulo.")

                consulta.usuarioInstitucionId = institutionId

                saveConsulta(consulta)

                _mensaje.value = "Cita guardada correctamente."
                _salir.value = true

            } catch (e: Exception) {
                _mensaje.value = "Ocurrió un error inesperado al guardar la cita."

            } finally {
                _isLoading.value = false
            }
        }
    }

}