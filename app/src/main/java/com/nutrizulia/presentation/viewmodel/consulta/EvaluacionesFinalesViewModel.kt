package com.nutrizulia.presentation.viewmodel.consulta

import android.util.Log
import androidx.lifecycle.*
import com.nutrizulia.data.local.enum.TipoValorCalculado
import com.nutrizulia.domain.model.catalog.GrupoEtario
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import com.nutrizulia.domain.model.collection.Diagnostico
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.catalog.GetEnfermedades
import com.nutrizulia.domain.usecase.catalog.GetGrupoEtario
import com.nutrizulia.domain.usecase.catalog.GetParametroCrecimientoNinoEdad
import com.nutrizulia.domain.usecase.catalog.GetParametroCrecimientoPediatricoEdad
import com.nutrizulia.domain.usecase.catalog.GetParametroCrecimientoPediatricoLongitud
import com.nutrizulia.domain.usecase.catalog.GetReglaInterpretacionImc
import com.nutrizulia.domain.usecase.catalog.GetReglaInterpretacionZScore
import com.nutrizulia.domain.usecase.catalog.GetRiesgosBiologicos
import com.nutrizulia.domain.usecase.collection.GetDiagnosticosByConsultaId
import com.nutrizulia.domain.usecase.collection.GetEvaluacionesAntropometricasByConsultaId
import com.nutrizulia.util.Utils
import com.nutrizulia.util.Utils.calcularIMC
import com.nutrizulia.util.Utils.calcularZScoreOMS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.collections.orEmpty

@HiltViewModel
class EvaluacionesFinalesViewModel @Inject constructor(
    private val getDiagnosticos: GetRiesgosBiologicos,
    private val getEnfermedades: GetEnfermedades,
    private val getDiagnosticosByConsultaId: GetDiagnosticosByConsultaId,
    private val getEvaluacionesAntropometricasByConsultaId: GetEvaluacionesAntropometricasByConsultaId,
    private val getGrupoEtario: GetGrupoEtario,
    private val getParametroCrecimientoNinoEdad: GetParametroCrecimientoNinoEdad,
    private val getParametroCrecimientoPediatricoEdad: GetParametroCrecimientoPediatricoEdad,
    private val getParametroCrecimientoPediatricoLongitud: GetParametroCrecimientoPediatricoLongitud,
    private val getReglaInterpretacionImc: GetReglaInterpretacionImc,
    private val getReglaInterpretacionZScore: GetReglaInterpretacionZScore
) : ViewModel() {

    // --- State & Events ---
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    // --- Data for UI ---
    private val _diagnosticosDisponibles = MutableLiveData<List<RiesgoBiologico>>()
    val diagnosticosDisponibles: LiveData<List<RiesgoBiologico>> = _diagnosticosDisponibles

    private val _diagnosticosIniciales = MutableLiveData<List<Diagnostico>>()

    private val _diagnosticosSeleccionados = MediatorLiveData<List<RiesgoBiologico>>()
    val diagnosticosSeleccionados: LiveData<List<RiesgoBiologico>> = _diagnosticosSeleccionados

    private val _evaluacionesCalculadas = MutableLiveData<List<EvaluacionAntropometrica>>()
    val evaluacionesCalculadas: LiveData<List<EvaluacionAntropometrica>> = _evaluacionesCalculadas

    init {
        // Mapea los diagnósticos seleccionados cuando cambian los diagnósticos iniciales o el catálogo de diagnósticos
        _diagnosticosSeleccionados.addSource(_diagnosticosIniciales) { mapearDiagnosticosYDiagnosticos() }
        _diagnosticosSeleccionados.addSource(_diagnosticosDisponibles) { mapearDiagnosticosYDiagnosticos() }
    }

    fun loadInitialData(paciente: Paciente, consultaId: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                coroutineScope {
                    val diagnosticosDisponiblesDeferred = async {
                        val edadMeses = Utils.calcularEdadEnMeses(paciente.fechaNacimiento)
                        getDiagnosticos(paciente.genero.first().uppercaseChar().toString(), edadMeses)
                    }

                    if (consultaId != null) {
                        val diagnosticosDeferred = async { getDiagnosticosByConsultaId(consultaId) }
                        val evaluacionesDeferred = async { getEvaluacionesAntropometricasByConsultaId(consultaId) }

                        _diagnosticosIniciales.value = diagnosticosDeferred.await()
                        _evaluacionesCalculadas.value = evaluacionesDeferred.await()
                    }

                    _diagnosticosDisponibles.value = diagnosticosDisponiblesDeferred.await()
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar datos de diagnóstico: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun mapearDiagnosticosYDiagnosticos() {
        val diagnosticos: List<Diagnostico> = _diagnosticosIniciales.value.orEmpty()
        val catalogo: List<RiesgoBiologico> = _diagnosticosDisponibles.value.orEmpty()
        val diagnosticosSeleccionados: List<RiesgoBiologico> = diagnosticos
            .mapNotNull { diag -> catalogo.find { it.id == diag.riesgoBiologicoId } }
        _diagnosticosSeleccionados.value = diagnosticosSeleccionados
    }

    fun agregarDiagnostico(diagnostico: RiesgoBiologico) {
        val diagnosticosActuales: MutableList<RiesgoBiologico> = _diagnosticosSeleccionados.value.orEmpty().toMutableList()
        if (!diagnosticosActuales.any { it.id == diagnostico.id }) {
            diagnosticosActuales.add(diagnostico)
            _diagnosticosSeleccionados.value = diagnosticosActuales
        }
    }

    fun eliminarDiagnostico(diagnostico: RiesgoBiologico) {
        val diagnosticosActuales: MutableList<RiesgoBiologico> = _diagnosticosSeleccionados.value.orEmpty().toMutableList()
        diagnosticosActuales.removeAll { it.id == diagnostico.id }
        _diagnosticosSeleccionados.value = diagnosticosActuales
    }

    fun createDiagnosticosEntities(consultaId: String): List<Diagnostico> {
        val diagnosticosExistentes = _diagnosticosIniciales.value.orEmpty()
        
        return diagnosticosSeleccionados.value.orEmpty().map { diagnostico ->
            // Buscar si ya existe un diagnóstico para este diagnóstico que NO esté eliminado
            val diagnosticoExistente = diagnosticosExistentes.find { 
                it.riesgoBiologicoId == diagnostico.id && !it.isDeleted 
            }
            
            Diagnostico(
                id = diagnosticoExistente?.id ?: Utils.generarUUID(), // Reutilizar ID existente solo si no está eliminado
                consultaId = consultaId,
                riesgoBiologicoId = diagnostico.id,
                enfermedadId = null,
                isPrincipal = false,
                updatedAt = LocalDateTime.now(),
                isDeleted = false,
                isSynced = false
            )
        }
    }

    fun createEvaluacionesAntropometricasEntities(): List<EvaluacionAntropometrica> {
        return _evaluacionesCalculadas.value.orEmpty().map { evaluacion ->
            evaluacion.copy(
                updatedAt = LocalDateTime.now(),
                isDeleted = false,
                isSynced = false
            )
        }
    }

    // --- Lógica de Evaluación Antropométrica (Movida y Refactorizada) ---

    fun performAnthropometricEvaluation(
        paciente: Paciente,
        detalle: DetalleAntropometrico,
        consulta: Consulta
    ) {
        viewModelScope.launch {
            runCatching {
                val edadMeses = Utils.calcularEdadEnMeses(paciente.fechaNacimiento)
                val grupoEtario = getGrupoEtario(edadMeses) ?: throw IllegalStateException("Grupo etario no encontrado para $edadMeses meses")
                val evaluacionesActuales = _evaluacionesCalculadas.value.orEmpty().toMutableList()

                when (grupoEtario.id) {
                    1 -> evaluateInfant(paciente, detalle, consulta, grupoEtario, evaluacionesActuales)
                    2, 3 -> evaluateChildAndAdolescent(paciente, detalle, consulta, grupoEtario, evaluacionesActuales)
                    else -> evaluateAdult(detalle, consulta, evaluacionesActuales)
                }

                _evaluacionesCalculadas.postValue(evaluacionesActuales)
            }.onFailure { e ->
                _mensaje.postValue("Error en evaluación antropométrica: ${e.localizedMessage}")
                Log.e("EvaluacionAntro", "Error en evaluación", e)
            }
        }
    }

    private suspend fun evaluateInfant(
        paciente: Paciente,
        detalle: DetalleAntropometrico,
        consulta: Consulta,
        grupoEtario: GrupoEtario,
        evaluations: MutableList<EvaluacionAntropometrica>
    ) {
        val genero = paciente.genero.first().uppercaseChar().toString()
        val edadDias = Utils.calcularEdadEnDias(paciente.fechaNacimiento)
        val longitud = detalle.talla ?: detalle.altura ?: throw IllegalStateException("Debe registrar talla o altura")
        val tipoMedicion = if (detalle.altura != null) "A" else "T"

        getParametroCrecimientoPediatricoEdad(grupoEtario.id, genero, edadDias).forEach { param ->
            val valor: Double? = when (param.tipoIndicadorId) {
                1 -> if (detalle.peso != null) calcularIMC(detalle.peso, longitud).imc else null
                2 -> detalle.perimetroCefalico
                4 -> detalle.peso
                6 -> detalle.talla
                7 -> detalle.altura
                else -> null
            }
            valor?.let { processZScoreEvaluation(it, param.tipoIndicadorId, param.lambda, param.mu, param.sigma, consulta.id, detalle.id, evaluations) }
        }

        getParametroCrecimientoPediatricoLongitud(grupoEtario.id, genero, longitud, tipoMedicion)?.let { param ->
            detalle.peso?.let { processZScoreEvaluation(it, param.tipoIndicadorId, param.lambda, param.mu, param.sigma, consulta.id, detalle.id, evaluations) }
        }
    }

    private suspend fun evaluateChildAndAdolescent(
        paciente: Paciente,
        detalle: DetalleAntropometrico,
        consulta: Consulta,
        grupoEtario: GrupoEtario,
        evaluations: MutableList<EvaluacionAntropometrica>
    ) {
        val genero = paciente.genero.first().uppercaseChar().toString()
        val edadMeses = Utils.calcularEdadEnMeses(paciente.fechaNacimiento)
        val altura = detalle.altura ?: throw IllegalStateException("Debe registrar la altura")

        val todosLosParametros = getParametroCrecimientoNinoEdad(2, genero, edadMeses) + getParametroCrecimientoNinoEdad(3, genero, edadMeses)
        todosLosParametros.forEach { param ->
            val valor: Double? = when (param.tipoIndicadorId) {
                1 -> if (detalle.peso != null) calcularIMC(detalle.peso, altura).imc else null
                4 -> detalle.peso
                7 -> altura
                else -> null
            }
            valor?.let { processZScoreEvaluation(it, param.tipoIndicadorId, param.lambda, param.mu, param.sigma, consulta.id, detalle.id, evaluations) }
        }
    }

    private suspend fun evaluateAdult(
        detalle: DetalleAntropometrico,
        consulta: Consulta,
        evaluations: MutableList<EvaluacionAntropometrica>
    ) {
        if (detalle.peso != null && detalle.altura != null) {
            val imcResult = calcularIMC(detalle.peso, detalle.altura)
            val diagnostico = getReglaInterpretacionImc(8, imcResult.imc) ?: "Sin diagnóstico"
            upsertEvaluationInList(evaluations, 8, TipoValorCalculado.IMC, imcResult.imc, diagnostico, consulta.id, detalle.id)
        }
    }

    private suspend fun processZScoreEvaluation(
        value: Double,
        tipoIndicadorId: Int,
        lambda: Double,
        mu: Double,
        sigma: Double,
        consultaId: String,
        detalleId: String,
        evaluations: MutableList<EvaluacionAntropometrica>
    ) {
        calcularZScoreOMS(value, lambda, mu, sigma)?.let { result ->
            val diagnostic = getReglaInterpretacionZScore(tipoIndicadorId, result.zScore) ?: "Sin diagnóstico"
            upsertEvaluationInList(evaluations, tipoIndicadorId, TipoValorCalculado.Z_SCORE, result.zScore, diagnostic, consultaId, detalleId)
            upsertEvaluationInList(evaluations, tipoIndicadorId, TipoValorCalculado.PERCENTIL, result.percentil, diagnostic, consultaId, detalleId)
        }
    }

    private fun upsertEvaluationInList(
        evaluations: MutableList<EvaluacionAntropometrica>,
        indicatorId: Int,
        valueType: TipoValorCalculado,
        calculatedValue: Double,
        diagnostic: String,
        consultaId: String,
        detalleId: String
    ) {
        val existingIndex = evaluations.indexOfFirst { it.tipoIndicadorId == indicatorId && it.tipoValorCalculado == valueType }

        if (existingIndex != -1) {
            // Actualizar evaluación existente manteniendo el ID original
            evaluations[existingIndex] = evaluations[existingIndex].copy(
                valorCalculado = calculatedValue,
                diagnosticoAntropometrico = diagnostic,
                updatedAt = LocalDateTime.now(),
                isDeleted = false,
                isSynced = false
            )
        } else {
            // Buscar si existe una evaluación previa en las evaluaciones iniciales para reutilizar su ID
            // Solo reutilizar ID si la evaluación NO está eliminada
            val evaluacionesIniciales = _evaluacionesCalculadas.value.orEmpty()
            val evaluacionExistente = evaluacionesIniciales.find { 
                it.tipoIndicadorId == indicatorId && it.tipoValorCalculado == valueType && !it.isDeleted
            }
            
            evaluations.add(
                EvaluacionAntropometrica(
                    id = evaluacionExistente?.id ?: Utils.generarUUID(), // Reutilizar ID existente solo si no está eliminado
                    consultaId = consultaId,
                    detalleAntropometricoId = detalleId,
                    tipoIndicadorId = indicatorId,
                    valorCalculado = calculatedValue,
                    tipoValorCalculado = valueType,
                    diagnosticoAntropometrico = diagnostic,
                    fechaEvaluacion = LocalDate.now(),
                    updatedAt = LocalDateTime.now(),
                    isDeleted = false,
                    isSynced = false
                )
            )
        }
    }
}