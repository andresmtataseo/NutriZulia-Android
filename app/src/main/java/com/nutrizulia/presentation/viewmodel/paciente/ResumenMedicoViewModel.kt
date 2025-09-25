package com.nutrizulia.presentation.viewmodel.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import com.nutrizulia.domain.model.collection.DetallePediatrico
import com.nutrizulia.domain.model.collection.DetalleVital
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.model.catalog.TipoIndicador
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.domain.usecase.collection.GetLatestDetalleAntropometricoByPacienteId
import com.nutrizulia.domain.usecase.collection.GetLatestDetalleMetabolicoByPacienteId
import com.nutrizulia.domain.usecase.collection.GetLatestDetalleVitalByPacienteId
import com.nutrizulia.domain.usecase.collection.GetLatestDetalleObstetriciaByPacienteId
import com.nutrizulia.domain.usecase.collection.GetLatestDetallePediatricoByPacienteId
import com.nutrizulia.domain.usecase.collection.GetLatestEvaluacionesAntropometricasByPacienteId
import com.nutrizulia.domain.usecase.collection.GetDiagnosticosConDescripcionesByPacienteIdUseCase
import com.nutrizulia.data.local.pojo.DiagnosticoConDescripcion
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumenMedicoViewModel @Inject constructor(
    private val getPaciente: GetPacienteById,
    private val getLatestDetalleAntropometrico: GetLatestDetalleAntropometricoByPacienteId,
    private val getLatestDetalleMetabolico: GetLatestDetalleMetabolicoByPacienteId,
    private val getLatestDetalleVital: GetLatestDetalleVitalByPacienteId,
    private val getLatestDetalleObstetricia: GetLatestDetalleObstetriciaByPacienteId,
    private val getLatestDetallePediatrico: GetLatestDetallePediatricoByPacienteId,
    private val getLatestEvaluacionesAntropometricas: GetLatestEvaluacionesAntropometricasByPacienteId,
    private val getDiagnosticosConDescripciones: GetDiagnosticosConDescripcionesByPacienteIdUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    private val _antropometricos = MutableLiveData<DetalleAntropometrico?>()
    val antropometricos: LiveData<DetalleAntropometrico?> = _antropometricos

    private val _metabolicos = MutableLiveData<DetalleMetabolico?>()
    val metabolicos: LiveData<DetalleMetabolico?> = _metabolicos

    private val _vitales = MutableLiveData<DetalleVital?>()
    val vitales: LiveData<DetalleVital?> = _vitales

    private val _obstetricos = MutableLiveData<DetalleObstetricia?>()
    val obstetricos: LiveData<DetalleObstetricia?> = _obstetricos

    private val _pediatricos = MutableLiveData<DetallePediatrico?>()
    val pediatricos: LiveData<DetallePediatrico?> = _pediatricos

    private val _evaluacionesAntropometricas = MutableLiveData<Map<TipoIndicador, EvaluacionAntropometrica>>()
    val evaluacionesAntropometricas: LiveData<Map<TipoIndicador, EvaluacionAntropometrica>> = _evaluacionesAntropometricas

    private val _diagnosticos = MutableLiveData<List<DiagnosticoConDescripcion>>()
    val diagnosticos: LiveData<List<DiagnosticoConDescripcion>> = _diagnosticos

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
                val pacienteJob = launch { obtenerPaciente(id) }
                val antropometricoJob = launch { obtenerAntropometrico(id) }
                val metabolicoJob = launch { obtenerMetabolico(id) }
                val vitalJob = launch { obtenerVital(id) }
                val obstetricoJob = launch { obtenerObstetrico(id) }
                val pediatricoJob = launch { obtenerPediátrico(id) }
                val evaluacionesJob = launch { obtenerEvaluacionesAntropometricas(id) }
                val diagnosticosJob = launch { obtenerDiagnosticos(id) }
                pacienteJob.join()
                antropometricoJob.join()
                metabolicoJob.join()
                vitalJob.join()
                obstetricoJob.join()
                pediatricoJob.join()
                evaluacionesJob.join()
                diagnosticosJob.join()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun obtenerPaciente(id: String) {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = Event("Error al buscar pacientes. No se ha seleccionado una institución.")
                _isLoading.value = false
                _salir.value = Event(true)
            }

            val result = getPaciente(idUsuarioInstitucion.value ?: 0, id)
            if (result != null) {
                _paciente.value = result
            } else {
                _mensaje.value = Event("No se encontraron datos.")
                _isLoading.value = false
                _salir.value = Event(true)
                return@launch
            }
            _isLoading.value = false
        }
    }

    private fun obtenerAntropometrico(id: String) {
        viewModelScope.launch {
            try {
                val detalle = getLatestDetalleAntropometrico(id)
                _antropometricos.value = detalle
            } catch (e: Exception) {
                // Manejo silencioso del error - el detalle simplemente no estará disponible
            }
        }
    }

    private fun obtenerMetabolico(id: String) {
        viewModelScope.launch {
            try {
                val detalle = getLatestDetalleMetabolico(id)
                _metabolicos.value = detalle
            } catch (e: Exception) {
                // Manejo silencioso del error - el detalle simplemente no estará disponible
            }
        }
    }

    private fun obtenerVital(id: String) {
        viewModelScope.launch {
            try {
                val detalle = getLatestDetalleVital(id)
                _vitales.value = detalle
            } catch (e: Exception) {
                // Manejo silencioso del error - el detalle simplemente no estará disponible
            }
        }
    }

    private fun obtenerObstetrico(id: String) {
        viewModelScope.launch {
            try {
                val detalle = getLatestDetalleObstetricia(id)
                _obstetricos.value = detalle
            } catch (e: Exception) {
                // Manejo silencioso del error - el detalle simplemente no estará disponible
            }
        }
    }

    private fun obtenerPediátrico(id: String) {
        viewModelScope.launch {
            try {
                val detalle = getLatestDetallePediatrico(id)
                _pediatricos.value = detalle
            } catch (e: Exception) {
                // Manejo silencioso del error - el detalle simplemente no estará disponible
            }
        }
    }

    private fun obtenerEvaluacionesAntropometricas(id: String) {
        viewModelScope.launch {
            try {
                val evaluaciones = getLatestEvaluacionesAntropometricas(id)
                _evaluacionesAntropometricas.value = evaluaciones
            } catch (e: Exception) {
                // Manejo silencioso del error - las evaluaciones simplemente no estarán disponibles
            }
        }
    }

    private fun obtenerDiagnosticos(id: String) {
        viewModelScope.launch {
            try {
                val diagnosticos = getDiagnosticosConDescripciones(id)
                _diagnosticos.value = diagnosticos
            } catch (e: Exception) {
                // Manejo silencioso del error - los diagnósticos simplemente no estarán disponibles
            }
        }
    }
}
