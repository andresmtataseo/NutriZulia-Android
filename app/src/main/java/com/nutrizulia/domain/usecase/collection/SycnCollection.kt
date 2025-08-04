package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.*
import com.nutrizulia.domain.model.SyncResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import javax.inject.Inject

data class SyncBatchResult(
    val batchName: String,
    val results: List<SyncResult<*>>,
    val hasErrors: Boolean = results.any { it !is SyncResult.Success }
)

data class CollectionSyncResults(
    val batch1Result: SyncBatchResult, // Representantes
    val batch2Result: SyncBatchResult, // Pacientes y PacienteRepresentante
    val batch3Result: SyncBatchResult, // Consultas
    val batch4Result: SyncBatchResult, // Detalles de Consulta
    val batch5Result: SyncBatchResult  // Evaluaciones
) {
    fun hasErrors(): Boolean {
        return listOf(batch1Result, batch2Result, batch3Result, batch4Result, batch5Result)
            .any { it.hasErrors }
    }
    
    fun getSuccessCount(): Int {
        return listOf(batch1Result, batch2Result, batch3Result, batch4Result, batch5Result)
            .sumOf { batch -> batch.results.count { it is SyncResult.Success } }
    }
    
    fun getTotalOperations(): Int {
        return listOf(batch1Result, batch2Result, batch3Result, batch4Result, batch5Result)
            .sumOf { it.results.size }
    }
}

class SyncCollection @Inject constructor(
    private val consultaRepository: ConsultaRepository,
    private val antropometricoRepository: DetalleAntropometricoRepository,
    private val metabolicoRepository: DetalleMetabolicoRepository,
    private val obstetriciaRepository: DetalleObstetriciaRepository,
    private val pediatricoRepository: DetallePediatricoRepository,
    private val vitalRepository: DetalleVitalRepository,
    private val diagnosticoRepository: DiagnosticoRepository,
    private val evaluacionRepository: EvaluacionAntropometricaRepository,
    private val pacienteRepository: PacienteRepository,
    private val pacienteRepresentanteRepository: PacienteRepresentanteRepository,
    private val representanteRepository: RepresentanteRepository
) {
    /**
     * Sincroniza todas las colecciones respetando las dependencias del esquema de base de datos.
     * La sincronización se realiza en lotes secuenciales para evitar violaciones de foreign key.
     * 
     * Orden de dependencias:
     * 1. Representantes (sin dependencias)
     * 2. Pacientes y PacienteRepresentante (dependen de Representantes)
     * 3. Consultas (dependen de Pacientes)
     * 4. Detalles de Consulta (dependen de Consultas)
     * 5. Evaluaciones (dependen de Detalles Antropométricos)
     */
    suspend operator fun invoke(lastSyncTime: LocalDateTime = LocalDateTime.MIN): CollectionSyncResults {
        // Lote 1: Representantes (sin dependencias)
        val batch1Result = executeSyncBatch("Representantes") {
            listOf(
                runCatching { representanteRepository.sincronizarRepresentantes() }
                    .getOrElse { SyncResult.UnknownError(it) }
            )
        }
        
        // Si el lote 1 falla, no continuar
        if (batch1Result.hasErrors) {
            return createFailedResult(batch1Result)
        }
        
        // Lote 2: Pacientes y PacienteRepresentante (dependen de Representantes)
        val batch2Result = executeSyncBatch("Pacientes y Relaciones") {
            coroutineScope {
                val pacienteDeferred = async { 
                    runCatching { pacienteRepository.sincronizarPacientes() }
                        .getOrElse { SyncResult.UnknownError(it) }
                }
                val pacienteRepresentanteDeferred = async { 
                    runCatching { pacienteRepresentanteRepository.sincronizarPacientesRepresentantes() }
                        .getOrElse { SyncResult.UnknownError(it) }
                }
                awaitAll(pacienteDeferred, pacienteRepresentanteDeferred)
            }
        }
        
        // Si el lote 2 falla, no continuar
        if (batch2Result.hasErrors) {
            return createFailedResult(batch1Result, batch2Result)
        }
        
        // Lote 3: Consultas (dependen de Pacientes)
        val batch3Result = executeSyncBatch("Consultas") {
            listOf(
                runCatching { consultaRepository.sincronizarConsultas() }
                    .getOrElse { SyncResult.UnknownError(it) }
            )
        }
        
        // Si el lote 3 falla, no continuar
        if (batch3Result.hasErrors) {
            return createFailedResult(batch1Result, batch2Result, batch3Result)
        }
        
        // Lote 4: Detalles de Consulta y Diagnósticos (dependen de Consultas)
        val batch4Result = executeSyncBatch("Detalles de Consulta") {
            coroutineScope {
                val antropometricoDeferred = async { 
                    runCatching { antropometricoRepository.sincronizarDetallesAntropometricos() }
                        .getOrElse { SyncResult.UnknownError(it) }
                }
                val metabolicoDeferred = async { 
                    runCatching { metabolicoRepository.sincronizarDetallesMetabolicos() }
                        .getOrElse { SyncResult.UnknownError(it) }
                }
                val obstetriciaDeferred = async { 
                    runCatching { obstetriciaRepository.sincronizarDetallesObstetricias() }
                        .getOrElse { SyncResult.UnknownError(it) }
                }
                val pediatricoDeferred = async { 
                    runCatching { pediatricoRepository.sincronizarDetallesPediatricos() }
                        .getOrElse { SyncResult.UnknownError(it) }
                }
                val vitalDeferred = async { 
                    runCatching { vitalRepository.sincronizarDetallesVitales() }
                        .getOrElse { SyncResult.UnknownError(it) }
                }
                val diagnosticoDeferred = async { 
                    runCatching { diagnosticoRepository.sincronizarDiagnosticos() }
                        .getOrElse { SyncResult.UnknownError(it) }
                }
                awaitAll(
                    antropometricoDeferred, metabolicoDeferred, obstetriciaDeferred,
                    pediatricoDeferred, vitalDeferred, diagnosticoDeferred
                )
            }
        }
        
        // Lote 5: Evaluaciones (dependen de Detalles Antropométricos)
        val batch5Result = executeSyncBatch("Evaluaciones") {
            listOf(
                runCatching { evaluacionRepository.sincronizarEvaluacionesAntropometricas() }
                    .getOrElse { SyncResult.UnknownError(it) }
            )
        }
        
        return CollectionSyncResults(
            batch1Result = batch1Result,
            batch2Result = batch2Result,
            batch3Result = batch3Result,
            batch4Result = batch4Result,
            batch5Result = batch5Result
        )
    }
    
    private suspend fun executeSyncBatch(
        batchName: String,
        syncOperations: suspend () -> List<SyncResult<*>>
    ): SyncBatchResult {
        return try {
            val results = syncOperations()
            SyncBatchResult(batchName, results)
        } catch (e: Exception) {
            SyncBatchResult(
                batchName, 
                listOf(SyncResult.UnknownError(e)),
                hasErrors = true
            )
        }
    }
    
    private fun createFailedResult(
        vararg completedBatches: SyncBatchResult
    ): CollectionSyncResults {
        val emptyBatch = SyncBatchResult("No ejecutado", emptyList(), hasErrors = false)
        val batches = completedBatches.toList() + 
            List(5 - completedBatches.size) { emptyBatch }
        
        return CollectionSyncResults(
            batch1Result = batches.getOrElse(0) { emptyBatch },
            batch2Result = batches.getOrElse(1) { emptyBatch },
            batch3Result = batches.getOrElse(2) { emptyBatch },
            batch4Result = batches.getOrElse(3) { emptyBatch },
            batch5Result = batches.getOrElse(4) { emptyBatch }
        )
    }
}