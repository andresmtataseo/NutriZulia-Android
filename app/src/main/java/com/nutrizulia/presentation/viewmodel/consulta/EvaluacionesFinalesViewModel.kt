package com.nutrizulia.presentation.viewmodel.consulta

import android.util.Log
import androidx.lifecycle.*
import com.nutrizulia.data.local.enum.TipoValorCalculado
import com.nutrizulia.domain.model.catalog.GrupoEtario
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
import com.nutrizulia.domain.model.catalog.Enfermedad
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

/**
 * Clase que representa un diagnóstico para mostrar en la UI
 * Puede ser un diagnóstico normal o uno con enfermedad específica
 */
data class DiagnosticoParaUI(
    val riesgoBiologico: RiesgoBiologico,
    val enfermedad: Enfermedad? = null,
    val diagnosticoCompleto: Diagnostico? = null // Para diagnósticos con enfermedad específica
) {
    val nombreCompleto: String
        get() = if (enfermedad != null) {
            "${riesgoBiologico.nombre} - ${enfermedad.nombre}"
        } else {
            riesgoBiologico.nombre
        }
}

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

    private val _enfermedadesDisponibles = MutableLiveData<List<Enfermedad>>()
    val enfermedadesDisponibles: LiveData<List<Enfermedad>> = _enfermedadesDisponibles

    private val _diagnosticosIniciales = MutableLiveData<List<Diagnostico>>()
    private val _diagnosticosHistoricos = MutableLiveData<List<Diagnostico>>()
    private val _consultaActualId = MutableLiveData<String?>()

    private val _diagnosticosSeleccionados = MediatorLiveData<List<DiagnosticoParaUI>>()
    val diagnosticosSeleccionados: LiveData<List<DiagnosticoParaUI>> = _diagnosticosSeleccionados
    
    private val _diagnosticosHistoricosRiesgo = MediatorLiveData<List<RiesgoBiologico>>()
    val diagnosticosHistoricosRiesgo: LiveData<List<RiesgoBiologico>> = _diagnosticosHistoricosRiesgo

    private val _evaluacionesCalculadas = MutableLiveData<List<EvaluacionAntropometrica>>()
    val evaluacionesCalculadas: LiveData<List<EvaluacionAntropometrica>> = _evaluacionesCalculadas

    private val _esPrimeraConsulta = MutableLiveData<Boolean>()
    val esPrimeraConsulta: LiveData<Boolean> = _esPrimeraConsulta

    private val _tieneDiagnosticoPrincipal = MutableLiveData<Boolean>()
    val tieneDiagnosticoPrincipal: LiveData<Boolean> = _tieneDiagnosticoPrincipal

    // Almacenar el género del paciente para búsquedas de enfermedades
    private var generoActual: String = ""

    init {
        // Configurar MediatorLiveData para mapear diagnósticos seleccionados
        _diagnosticosSeleccionados.addSource(_diagnosticosIniciales) { mapearDiagnosticosSeleccionados() }
        _diagnosticosSeleccionados.addSource(_diagnosticosDisponibles) { mapearDiagnosticosSeleccionados() }
        _diagnosticosSeleccionados.addSource(_enfermedadesDisponibles) { mapearDiagnosticosSeleccionados() }
        
        // Configurar MediatorLiveData para mapear diagnósticos históricos
        _diagnosticosHistoricosRiesgo.addSource(_diagnosticosHistoricos) { mapearDiagnosticosHistoricos() }
        _diagnosticosHistoricosRiesgo.addSource(_diagnosticosDisponibles) { mapearDiagnosticosHistoricos() }
        _diagnosticosHistoricosRiesgo.addSource(_consultaActualId) { mapearDiagnosticosHistoricos() }
    }

    fun loadInitialData(paciente: Paciente, consultaId: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Guardar el ID de la consulta actual y el género del paciente
                _consultaActualId.value = consultaId
                generoActual = paciente.genero.first().uppercaseChar().toString()
                
                coroutineScope {
                    val diagnosticosDisponiblesDeferred = async {
                        val edadMeses = Utils.calcularEdadEnMeses(paciente.fechaNacimiento)
                        getDiagnosticos(generoActual, edadMeses)
                    }
                    
                    // Cargar diagnósticos históricos del paciente
                    val diagnosticosHistoricosDeferred = async {
                        getDiagnosticosHistoricosByPacienteId(paciente.id)
                    }
                    
                    // Cargar enfermedades para mapear diagnósticos existentes
                    val enfermedadesDeferred = async {
                        getEnfermedades(generoActual, "")
                    }

                    if (consultaId != null) {
                        val diagnosticosDeferred = async { getDiagnosticosByConsultaId(consultaId) }
                        val evaluacionesDeferred = async { getEvaluacionesAntropometricasByConsultaId(consultaId) }

                        _diagnosticosIniciales.value = diagnosticosDeferred.await()
                        _evaluacionesCalculadas.value = evaluacionesDeferred.await()
                    }

                    _diagnosticosDisponibles.value = diagnosticosDisponiblesDeferred.await()
                    _diagnosticosHistoricos.value = diagnosticosHistoricosDeferred.await()
                    _enfermedadesDisponibles.value = enfermedadesDeferred.await()
                    
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

    /**
     * Carga las enfermedades disponibles filtradas por género y nombre
     */
    fun loadEnfermedades(nombreFiltro: String = "") {
        viewModelScope.launch {
            try {
                val enfermedades = getEnfermedades(generoActual, nombreFiltro)
                _enfermedadesDisponibles.value = enfermedades
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar enfermedades: ${e.localizedMessage}"
            }
        }
    }

    private suspend fun verificarTipoConsulta(pacienteId: String) {
        // Verificar si es primera consulta del paciente
        val esPrimeraConsulta = !countConsultaByPacienteIdUseCase(pacienteId)
        _esPrimeraConsulta.value = esPrimeraConsulta
    }

    /**
     * Mapea los diagnósticos iniciales de la consulta actual a objetos DiagnosticoParaUI
     */
    private fun mapearDiagnosticosSeleccionados() {
        val diagnosticosIniciales = _diagnosticosIniciales.value.orEmpty()
        val catalogo = _diagnosticosDisponibles.value.orEmpty()
        val enfermedades = _enfermedadesDisponibles.value.orEmpty()
        
        val diagnosticosSeleccionados = diagnosticosIniciales
            .mapNotNull { diagnostico -> 
                val riesgoBiologico = catalogo.find { it.id == diagnostico.riesgoBiologicoId }
                if (riesgoBiologico != null) {
                    val enfermedad = if (diagnostico.enfermedadId != null) {
                        enfermedades.find { it.id == diagnostico.enfermedadId }
                    } else null
                    
                    DiagnosticoParaUI(
                        riesgoBiologico = riesgoBiologico,
                        enfermedad = enfermedad,
                        diagnosticoCompleto = diagnostico
                    )
                } else null
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
        
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty().toMutableList()
        diagnosticosActuales.add(DiagnosticoParaUI(riesgoBiologico = diagnostico))
        _diagnosticosSeleccionados.value = diagnosticosActuales
        
        actualizarEstadoDiagnosticoPrincipal()
    }
    
    /**
     * Agrega un diagnóstico con enfermedad específica
     */
    fun agregarDiagnosticoConEnfermedad(riesgoBiologico: RiesgoBiologico, enfermedad: Enfermedad) {
        // Validar que no sea duplicado
        if (esDiagnosticoDuplicado(riesgoBiologico)) {
            return
        }
        
        // Validar reglas de primera consulta
        if (!puedeAgregarDiagnostico()) {
            return
        }
        
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty().toMutableList()
        diagnosticosActuales.add(
            DiagnosticoParaUI(
                riesgoBiologico = riesgoBiologico,
                enfermedad = enfermedad
            )
        )
        _diagnosticosSeleccionados.value = diagnosticosActuales
        
        actualizarEstadoDiagnosticoPrincipal()
    }
    
    companion object {
        private const val DIAGNOSTICO_OTROS_KEYWORD = "OTROS"
        private const val FILTRO_MINIMO_CARACTERES = 1
        private const val MAX_DIAGNOSTICOS_ADICIONALES = 5
    }
    
    /**
     * Valida si se puede agregar un nuevo diagnóstico
     */
    private fun puedeAgregarDiagnostico(): Boolean {
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty()
        val esPrimera = _esPrimeraConsulta.value ?: false
        val tienePrincipal = _tieneDiagnosticoPrincipal.value ?: false
        
        return when {
            !esPrimera -> true // En consultas de seguimiento siempre se pueden agregar
            !tienePrincipal -> true // En primera consulta sin diagnóstico principal
            diagnosticosActuales.size < MAX_DIAGNOSTICOS_ADICIONALES -> true // Límite de diagnósticos adicionales
            else -> {
                _mensaje.value = "No se pueden agregar más diagnósticos adicionales (máximo $MAX_DIAGNOSTICOS_ADICIONALES)"
                false
            }
        }
    }
    
    /**
     * Valida si un diagnóstico ya está seleccionado (actual o histórico)
     */
    private fun esDiagnosticoDuplicado(diagnostico: RiesgoBiologico): Boolean {
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty()
        val diagnosticosHistoricos = _diagnosticosHistoricosRiesgo.value.orEmpty()
        
        val existeEnActuales = diagnosticosActuales.any { it.riesgoBiologico.id == diagnostico.id }
        val existeEnHistoricos = diagnosticosHistoricos.any { it.id == diagnostico.id }
        
        val esDuplicado = existeEnActuales || existeEnHistoricos
        
        if (esDuplicado) {
            mostrarMensajeDiagnosticoDuplicado(diagnostico, existeEnActuales)
        }
        
        return esDuplicado
    }
    
    private fun mostrarMensajeDiagnosticoDuplicado(diagnostico: RiesgoBiologico, existeEnActuales: Boolean) {
        val tipoExistente = if (existeEnActuales) "actual" else "histórico"
        _mensaje.value = "El diagnóstico '${diagnostico.nombre}' ya está registrado como $tipoExistente para este paciente"
    }

    /**
     * Elimina un diagnóstico de la lista de seleccionados
     */
    fun eliminarDiagnostico(diagnostico: DiagnosticoParaUI) {
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty().toMutableList()
        diagnosticosActuales.removeAll { it.riesgoBiologico.id == diagnostico.riesgoBiologico.id }
        _diagnosticosSeleccionados.value = diagnosticosActuales
        
        actualizarEstadoDiagnosticoPrincipal()
    }
    
    /**
     * Elimina un diagnóstico por RiesgoBiologico (para compatibilidad)
     */
    fun eliminarDiagnostico(diagnostico: RiesgoBiologico) {
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty().toMutableList()
        diagnosticosActuales.removeAll { it.riesgoBiologico.id == diagnostico.id }
        _diagnosticosSeleccionados.value = diagnosticosActuales
        
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
        
        return diagnosticosSeleccionados.mapIndexed { index, diagnosticoUI ->
            // Buscar si ya existe un diagnóstico para este diagnóstico que NO esté eliminado
            val diagnosticoExistente = diagnosticosExistentes.find { 
                it.riesgoBiologicoId == diagnosticoUI.riesgoBiologico.id && 
                it.enfermedadId == diagnosticoUI.enfermedad?.id &&
                !it.isDeleted 
            }
            
            // Determinar si es principal: solo en primera consulta y solo el primero
            val esPrincipal = esPrimeraConsulta && index == 0 && !(_tieneDiagnosticoPrincipal.value ?: false)
            
            Diagnostico(
                id = diagnosticoExistente?.id ?: Utils.generarUUID(), // Reutilizar ID existente solo si no está eliminado
                consultaId = consultaId,
                riesgoBiologicoId = diagnosticoUI.riesgoBiologico.id,
                enfermedadId = diagnosticoUI.enfermedad?.id, // Incluir ID de enfermedad si existe
                isPrincipal = esPrincipal,
                updatedAt = LocalDateTime.now(),
                isDeleted = false,
                isSynced = false
            )
        }
    }

    /**
     * Crea un diagnóstico específico con enfermedad asociada (para el caso "OTROS")
     */
    fun createDiagnosticoConEnfermedad(
        consultaId: String,
        riesgoBiologicoId: Int,
        enfermedadId: Int
    ): Diagnostico {
        val esPrimeraConsulta = _esPrimeraConsulta.value ?: false
        val diagnosticosActuales = _diagnosticosSeleccionados.value.orEmpty()
        
        // Determinar si es principal: solo en primera consulta y si no hay otros diagnósticos
        val esPrincipal = esPrimeraConsulta && diagnosticosActuales.isEmpty() && !(_tieneDiagnosticoPrincipal.value ?: false)
        
        return Diagnostico(
            id = Utils.generarUUID(),
            consultaId = consultaId,
            riesgoBiologicoId = riesgoBiologicoId,
            enfermedadId = enfermedadId,
            isPrincipal = esPrincipal,
            updatedAt = LocalDateTime.now(),
            isDeleted = false,
            isSynced = false
        )
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