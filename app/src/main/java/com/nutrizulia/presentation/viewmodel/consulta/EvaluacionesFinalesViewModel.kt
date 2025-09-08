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
import com.nutrizulia.domain.usecase.collection.GetDiagnosticosHistoricosByPacienteId
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
import com.nutrizulia.domain.usecase.collection.CountConsultaByPacienteIdUseCase

@HiltViewModel
class EvaluacionesFinalesViewModel @Inject constructor(
    private val getDiagnosticos: GetRiesgosBiologicos,
    private val getEnfermedades: GetEnfermedades,
    private val getDiagnosticosByConsultaId: GetDiagnosticosByConsultaId,
    private val getDiagnosticosHistoricosByPacienteId: GetDiagnosticosHistoricosByPacienteId,
    private val getEvaluacionesAntropometricasByConsultaId: GetEvaluacionesAntropometricasByConsultaId,
    private val getGrupoEtario: GetGrupoEtario,
    private val getParametroCrecimientoNinoEdad: GetParametroCrecimientoNinoEdad,
    private val getParametroCrecimientoPediatricoEdad: GetParametroCrecimientoPediatricoEdad,
    private val getParametroCrecimientoPediatricoLongitud: GetParametroCrecimientoPediatricoLongitud,
    private val getReglaInterpretacionImc: GetReglaInterpretacionImc,
    private val getReglaInterpretacionZScore: GetReglaInterpretacionZScore,
    private val countConsultaByPacienteIdUseCase: CountConsultaByPacienteIdUseCase
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
    private val _diagnosticosHistoricos = MutableLiveData<List<Diagnostico>>()
    private val _consultaActualId = MutableLiveData<String?>()
    
    val diagnosticosHistoricos: LiveData<List<Diagnostico>> = _diagnosticosHistoricos

    private val _diagnosticosSeleccionados = MediatorLiveData<List<RiesgoBiologico>>()
    val diagnosticosSeleccionados: LiveData<List<RiesgoBiologico>> = _diagnosticosSeleccionados
    
    private val _diagnosticosHistoricosRiesgo = MediatorLiveData<List<RiesgoBiologico>>()
    val diagnosticosHistoricosRiesgo: LiveData<List<RiesgoBiologico>> = _diagnosticosHistoricosRiesgo

    private val _evaluacionesCalculadas = MutableLiveData<List<EvaluacionAntropometrica>>()
    val evaluacionesCalculadas: LiveData<List<EvaluacionAntropometrica>> = _evaluacionesCalculadas

    private val _esPrimeraConsulta = MutableLiveData<Boolean>()
    val esPrimeraConsulta: LiveData<Boolean> = _esPrimeraConsulta

    private val _tieneDiagnosticoPrincipal = MutableLiveData<Boolean>()
    val tieneDiagnosticoPrincipal: LiveData<Boolean> = _tieneDiagnosticoPrincipal

    init {
        // Mapea los diagnósticos seleccionados cuando cambian los diagnósticos iniciales o el catálogo de diagnósticos
        _diagnosticosSeleccionados.addSource(_diagnosticosIniciales) { mapearDiagnosticosSeleccionados() }
        _diagnosticosSeleccionados.addSource(_diagnosticosDisponibles) { mapearDiagnosticosSeleccionados() }
        
        // Mapea los diagnósticos históricos cuando cambian los diagnósticos históricos, el catálogo o la consulta actual
        _diagnosticosHistoricosRiesgo.addSource(_diagnosticosHistoricos) { mapearDiagnosticosHistoricos() }
        _diagnosticosHistoricosRiesgo.addSource(_diagnosticosDisponibles) { mapearDiagnosticosHistoricos() }
        _diagnosticosHistoricosRiesgo.addSource(_consultaActualId) { mapearDiagnosticosHistoricos() }
    }

    fun loadInitialData(paciente: Paciente, consultaId: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Guardar el ID de la consulta actual
                _consultaActualId.value = consultaId
                
                coroutineScope {
                    val diagnosticosDisponiblesDeferred = async {
                        val edadMeses = Utils.calcularEdadEnMeses(paciente.fechaNacimiento)
                        getDiagnosticos(paciente.genero.first().uppercaseChar().toString(), edadMeses)
                    }
                    
                    // Cargar diagnósticos históricos del paciente
                    val diagnosticosHistoricosDeferred = async {
                        getDiagnosticosHistoricosByPacienteId(paciente.id)
                    }

                    if (consultaId != null) {
                        val diagnosticosDeferred = async { getDiagnosticosByConsultaId(consultaId) }
                        val evaluacionesDeferred = async { getEvaluacionesAntropometricasByConsultaId(consultaId) }

                        _diagnosticosIniciales.value = diagnosticosDeferred.await()
                        _evaluacionesCalculadas.value = evaluacionesDeferred.await()
                    }

                    _diagnosticosDisponibles.value = diagnosticosDisponiblesDeferred.await()
                    _diagnosticosHistoricos.value = diagnosticosHistoricosDeferred.await()
                    
                    // Verificar si es primera consulta para determinar si se puede registrar diagnóstico principal
                    verificarTipoConsulta(paciente.id)
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar datos de diagnóstico: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun verificarTipoConsulta(pacienteId: String) {
        // Verificar si es primera consulta del paciente
        val esPrimeraConsulta = !countConsultaByPacienteIdUseCase(pacienteId)
        _esPrimeraConsulta.value = esPrimeraConsulta
    }

    /**
     * Mapea los diagnósticos iniciales de la consulta actual a objetos RiesgoBiologico
     */
    private fun mapearDiagnosticosSeleccionados() {
        val diagnosticosIniciales = _diagnosticosIniciales.value.orEmpty()
        val catalogo = _diagnosticosDisponibles.value.orEmpty()
        
        val diagnosticosSeleccionados = diagnosticosIniciales
            .mapNotNull { diagnostico -> 
                catalogo.find { it.id == diagnostico.riesgoBiologicoId }
            }
        
        _diagnosticosSeleccionados.value = diagnosticosSeleccionados
        
        // Verificar si ya existe un diagnóstico principal
        val tieneDiagnosticoPrincipal = diagnosticosIniciales.any { it.isPrincipal }
        _tieneDiagnosticoPrincipal.value = tieneDiagnosticoPrincipal
    }
    
    /**
     * Mapea los diagnósticos históricos excluyendo los de la consulta actual
     */
    private fun mapearDiagnosticosHistoricos() {
        val todosLosHistoricos = _diagnosticosHistoricos.value.orEmpty()
        val catalogo = _diagnosticosDisponibles.value.orEmpty()
        val consultaActualId = _consultaActualId.value
        
        // Filtrar diagnósticos históricos excluyendo los de la consulta actual
        val diagnosticosHistoricosFiltrados = if (consultaActualId != null) {
            todosLosHistoricos.filter { it.consultaId != consultaActualId }
        } else {
            todosLosHistoricos
        }
        
        val diagnosticosHistoricosRiesgo = diagnosticosHistoricosFiltrados
            .mapNotNull { diagnostico ->
                catalogo.find { it.id == diagnostico.riesgoBiologicoId }
            }
            .distinctBy { it.id } // Eliminar duplicados por ID

        _diagnosticosHistoricosRiesgo.value = diagnosticosHistoricosRiesgo
    }

    /**
     * Agrega un nuevo diagnóstico validando duplicados y reglas de negocio
     */
    fun agregarDiagnostico(diagnostico: RiesgoBiologico) {
        // Validar que no sea duplicado
        if (esDiagnosticoDuplicado(diagnostico)) {
            return
        }
        
        // Validar reglas de primera consulta
        if (!puedeAgregarDiagnostico()) {
            return
        }
        
        // Agregar el diagnóstico
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty().toMutableList()
        diagnosticosActuales.add(diagnostico)
        _diagnosticosSeleccionados.value = diagnosticosActuales
        
        // Actualizar estado de diagnóstico principal
        actualizarEstadoDiagnosticoPrincipal()
    }
    
    /**
     * Valida si se puede agregar un nuevo diagnóstico según las reglas de negocio
     */
    private fun puedeAgregarDiagnostico(): Boolean {
        val esPrimeraConsulta = _esPrimeraConsulta.value == true
        val tieneDiagnosticoPrincipal = _tieneDiagnosticoPrincipal.value == true
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty()
        
        // En primera consulta, solo se permite un diagnóstico principal
        if (esPrimeraConsulta && tieneDiagnosticoPrincipal && diagnosticosActuales.isNotEmpty()) {
            _mensaje.value = "En primera consulta solo se permite un diagnóstico principal"
            return false
        }
        
        return true
    }
    
    /**
     * Valida si un diagnóstico ya está seleccionado (actual o histórico)
     */
    private fun esDiagnosticoDuplicado(diagnostico: RiesgoBiologico): Boolean {
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty()
        val diagnosticosHistoricos = _diagnosticosHistoricosRiesgo.value.orEmpty()
        
        // Verificar si ya existe en diagnósticos actuales
        val existeEnActuales = diagnosticosActuales.any { it.id == diagnostico.id }
        
        // Verificar si ya existe en diagnósticos históricos
        val existeEnHistoricos = diagnosticosHistoricos.any { it.id == diagnostico.id }
        
        val esDuplicado = existeEnActuales || existeEnHistoricos
        
        if (esDuplicado) {
            val tipoExistente = if (existeEnActuales) "actual" else "histórico"
            _mensaje.value = "El diagnóstico '${diagnostico.nombre}' ya está registrado como $tipoExistente para este paciente"
        }
        
        return esDuplicado
    }

    /**
     * Elimina un diagnóstico de la lista de seleccionados
     */
    fun eliminarDiagnostico(diagnostico: RiesgoBiologico) {
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty().toMutableList()
        diagnosticosActuales.removeAll { it.id == diagnostico.id }
        _diagnosticosSeleccionados.value = diagnosticosActuales
        
        // Actualizar estado de diagnóstico principal
        actualizarEstadoDiagnosticoPrincipal()
    }
    
    /**
     * Actualiza el estado de si existe un diagnóstico principal
     */
    private fun actualizarEstadoDiagnosticoPrincipal() {
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty()
        val esPrimeraConsulta = _esPrimeraConsulta.value == true
        
        val tieneDiagnosticoPrincipal = when {
            // En primera consulta, el primer diagnóstico es principal automáticamente
            esPrimeraConsulta && diagnosticosActuales.isNotEmpty() -> true
            // En consultas posteriores, verificar si hay diagnósticos marcados como principales
            !esPrimeraConsulta -> _diagnosticosIniciales.value?.any { it.isPrincipal } == true
            else -> false
        }
        
        _tieneDiagnosticoPrincipal.value = tieneDiagnosticoPrincipal
    }

    fun createDiagnosticosEntities(consultaId: String): List<Diagnostico> {
        val diagnosticosExistentes = _diagnosticosIniciales.value.orEmpty()
        val esPrimeraConsulta = _esPrimeraConsulta.value ?: false
        val diagnosticosSeleccionados = _diagnosticosSeleccionados.value.orEmpty()
        
        return diagnosticosSeleccionados.mapIndexed { index, diagnostico ->
            // Buscar si ya existe un diagnóstico para este diagnóstico que NO esté eliminado
            val diagnosticoExistente = diagnosticosExistentes.find { 
                it.riesgoBiologicoId == diagnostico.id && !it.isDeleted 
            }
            
            // Determinar si es principal: solo en primera consulta y solo el primero
            val esPrincipal = esPrimeraConsulta && index == 0 && !(_tieneDiagnosticoPrincipal.value ?: false)
            
            Diagnostico(
                id = diagnosticoExistente?.id ?: Utils.generarUUID(), // Reutilizar ID existente solo si no está eliminado
                consultaId = consultaId,
                riesgoBiologicoId = diagnostico.id,
                enfermedadId = null,
                isPrincipal = esPrincipal,
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