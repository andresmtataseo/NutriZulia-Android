package com.nutrizulia.presentation.viewmodel.consulta

import androidx.lifecycle.*
import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity
import com.nutrizulia.domain.model.catalog.*
import com.nutrizulia.domain.model.collection.*
import com.nutrizulia.domain.usecase.catalog.*
import com.nutrizulia.domain.usecase.collection.GetDiagnosticosByConsultaId
import com.nutrizulia.domain.usecase.collection.GetEvaluacionesAntropometricasByConsultaId
import com.nutrizulia.domain.usecase.collection.SaveConsulta
import com.nutrizulia.domain.usecase.collection.SaveDetalleAntropometrico
import com.nutrizulia.domain.usecase.collection.SaveDetalleMetabolico
import com.nutrizulia.domain.usecase.collection.SaveDetalleObstetricia
import com.nutrizulia.domain.usecase.collection.SaveDetallePediatrico
import com.nutrizulia.domain.usecase.collection.SaveDetalleVital
import com.nutrizulia.domain.usecase.collection.SaveDiagnosticos
import com.nutrizulia.domain.usecase.collection.SaveEvaluacionesAntropometricas
import com.nutrizulia.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EvaluacionesFinalesViewModel @Inject constructor(
    private val getRiesgosBiologicos: GetRiesgosBiologicos,
    private val getEnfermedades: GetEnfermedades,
    private val getDiagnosticosByConsultaId: GetDiagnosticosByConsultaId,

    private val saveConsulta: SaveConsulta,
    private val saveDetalleVital: SaveDetalleVital,
    private val saveDetalleAntropometrico: SaveDetalleAntropometrico,
    private val saveDetalleMetabolico: SaveDetalleMetabolico,
    private val saveDetalleObstetricia: SaveDetalleObstetricia,
    private val saveDetallePediatrico: SaveDetallePediatrico,
    private val saveDiagnosticos: SaveDiagnosticos,
    private val saveEvaluacionesAntropometricas: SaveEvaluacionesAntropometricas,

    private val getEvaluacionesAntropometricasByConsultaId: GetEvaluacionesAntropometricasByConsultaId,
    private val getGrupoEtario: GetGrupoEtario,
    private val getParametroCrecimientoNinoEdad: GetParametroCrecimientoNinoEdad,
    private val getParametroCrecimientoPediatricoEdad: GetParametroCrecimientoPediatricoEdad,
    private val getParametroCrecimientoPediatricoLongitud: GetParametroCrecimientoPediatricoLongitud,
    private val getReglaInterpretacionImc: GetReglaInterpretacionImc,
    //private val getReglasInterpretacionPercentil: GetReglasInterpretacionPercentil,
    private val getReglaInterpretacionZScore: GetReglaInterpretacionZScore,
) : ViewModel() {

    private val _riesgosBiologicosDisponibles = MutableLiveData<List<RiesgoBiologico>>()
    val riesgosBiologicosDisponibles: LiveData<List<RiesgoBiologico>> = _riesgosBiologicosDisponibles

    private val _evaluacionesCalculadas = MutableLiveData<List<EvaluacionAntropometrica>>()
    val evaluacionesCalculadas: LiveData<List<EvaluacionAntropometrica>> = _evaluacionesCalculadas

    fun loadDiagnosticData(paciente: Paciente, consultaId: String?) {
        viewModelScope.launch {
            val edadMeses = Utils.calcularEdadEnMeses(paciente.fechaNacimiento)
            _riesgosBiologicosDisponibles.value = getRiesgosBiologicos(
                paciente.genero.first().uppercaseChar().toString(),
                edadMeses
            )
            // Lógica para cargar diagnósticos existentes si aplica
        }
    }

    fun performAnthropometricEvaluation(
        paciente: Paciente,
        detalleAntropometrico: DetalleAntropometrico,
        consulta: Consulta
    ) {
        viewModelScope.launch {
            // Toda la lógica de `realizarEvaluacionAntropometrica` del ViewModel original iría aquí.
            // Al final, en lugar de actualizar un LiveData propio, actualiza uno local.
            // val nuevasEvaluaciones = ...
            // _evaluacionesCalculadas.value = nuevasEvaluaciones
        }
    }

    fun createDiagnosticosEntities(
        riesgosSeleccionados: List<RiesgoBiologico>,
        consultaId: String
    ): List<DiagnosticoEntity> {
        return riesgosSeleccionados.map { riesgo ->
            DiagnosticoEntity(
                id = Utils.generarUUID(),
                consultaId = consultaId,
                riesgoBiologicoId = riesgo.id,
                enfermedadId = null,
                isPrincipal = false,
                updatedAt = LocalDateTime.now()
            )
        }
    }
}