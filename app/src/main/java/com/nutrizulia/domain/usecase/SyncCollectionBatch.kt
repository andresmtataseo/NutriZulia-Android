package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.data.repository.collection.DetalleAntropometricoRepository
import com.nutrizulia.data.repository.collection.DetalleMetabolicoRepository
import com.nutrizulia.data.repository.collection.DetalleObstetriciaRepository
import com.nutrizulia.data.repository.collection.DetallePediatricoRepository
import com.nutrizulia.data.repository.collection.DetalleVitalRepository
import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import com.nutrizulia.data.repository.collection.EvaluacionAntropometricaRepository
import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.data.repository.collection.PacienteRepresentanteRepository
import com.nutrizulia.data.repository.collection.RepresentanteRepository
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.SyncResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Deferred
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Caso de uso para sincronización por lotes de todas las colecciones
 * Maneja la sincronización en el orden correcto y proporciona resultados detallados
 */
@Singleton
class SyncCollectionBatch @Inject constructor(
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
     * Resultado de sincronización para una tabla específica
     */
    data class TableSyncResult(
        val tableName: String,
        val isSuccess: Boolean,
        val successCount: Int,
        val failedCount: Int,
        val errorMessage: String? = null,
        val details: BatchSyncResult? = null
    )

    /**
     * Resultado completo de la sincronización por lotes
     */
    data class CollectionBatchSyncResults(
        val tableResults: List<TableSyncResult>,
        val overallSuccess: Boolean,
        val totalSuccessCount: Int,
        val totalFailedCount: Int,
        val summary: String
    ) {
        fun getTablesWithErrors(): List<TableSyncResult> {
            return tableResults.filter { !it.isSuccess }
        }
        
        fun getTotalProcessed(): Int {
            return totalSuccessCount + totalFailedCount
        }
    }

    /**
     * Ejecuta la sincronización por lotes de todas las colecciones
     * @return Resultado detallado de la sincronización
     */
    suspend fun invoke(): CollectionBatchSyncResults = coroutineScope {
        val results = mutableListOf<TableSyncResult>()
        var overallSuccess = true
        var totalSuccess = 0
        var totalFailed = 0

        try {
            // Paso 1: Sincronizar tablas dependientes en orden
            // TODO: Agregar sincronización de representantes primero
            // val representantesResult = syncRepresentantes()
            // results.add(representantesResult)
            // if (!representantesResult.isSuccess) {
            //     return@coroutineScope createFailureResult(results, "Error en sincronización de representantes")
            // }

            // Paso 2: Sincronizar pacientes (depende de representantes)
            val pacientesResult = syncPacientes()
            results.add(pacientesResult)
            if (!pacientesResult.isSuccess) {
                overallSuccess = false
                // Continuar con otras tablas independientes
            }

            // Paso 3: Sincronizar datos clínicos en paralelo (independientes entre sí)
            val clinicalDataJobs = listOf<Deferred<TableSyncResult>>(
                // async { syncConsultas() },
                // async { syncDetallesAntropometricos() },
                // async { syncDetallesVitales() },
                // async { syncDiagnosticos() },
                // async { syncEvaluacionesAntropometricas() }
            )

            val clinicalResults = clinicalDataJobs.awaitAll()
            results.addAll(clinicalResults)

            // Calcular totales
            results.forEach { result ->
                totalSuccess += result.successCount
                totalFailed += result.failedCount
                if (!result.isSuccess) {
                    overallSuccess = false
                }
            }

        } catch (e: Exception) {
            overallSuccess = false
            results.add(
                TableSyncResult(
                    tableName = "Sistema",
                    isSuccess = false,
                    successCount = 0,
                    failedCount = 0,
                    errorMessage = "Error general: ${e.message}"
                )
            )
        }

        return@coroutineScope CollectionBatchSyncResults(
            tableResults = results,
            overallSuccess = overallSuccess,
            totalSuccessCount = totalSuccess,
            totalFailedCount = totalFailed,
            summary = generateSummary(results, overallSuccess, totalSuccess, totalFailed)
        )
    }

    private suspend fun syncPacientes(): TableSyncResult {
        return try {
            when (val result = pacienteRepository.sincronizarPacientesBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Pacientes",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Pacientes",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Pacientes",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Pacientes",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Pacientes",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private fun generateSummary(
        results: List<TableSyncResult>,
        overallSuccess: Boolean,
        totalSuccess: Int,
        totalFailed: Int
    ): String {
        val totalProcessed = totalSuccess + totalFailed
        val tablesWithErrors = results.filter { !it.isSuccess }
        
        return buildString {
            if (overallSuccess) {
                append("✅ Sincronización completada exitosamente\n")
            } else {
                append("⚠️ Sincronización completada con errores\n")
            }
            
            append("📊 Resumen: $totalSuccess exitosos, $totalFailed fallidos de $totalProcessed registros\n")
            
            if (tablesWithErrors.isNotEmpty()) {
                append("\n❌ Tablas con errores:\n")
                tablesWithErrors.forEach { table ->
                    append("• ${table.tableName}: ${table.errorMessage ?: "Error desconocido"}\n")
                }
            }
            
            append("\n📋 Detalle por tabla:\n")
            results.forEach { table ->
                val status = if (table.isSuccess) "✅" else "❌"
                append("$status ${table.tableName}: ${table.successCount} exitosos, ${table.failedCount} fallidos\n")
            }
        }
    }

    private fun createFailureResult(
        currentResults: List<TableSyncResult>,
        errorMessage: String
    ): CollectionBatchSyncResults {
        val totalSuccess = currentResults.sumOf { it.successCount }
        val totalFailed = currentResults.sumOf { it.failedCount }
        
        return CollectionBatchSyncResults(
            tableResults = currentResults,
            overallSuccess = false,
            totalSuccessCount = totalSuccess,
            totalFailedCount = totalFailed,
            summary = "❌ Sincronización interrumpida: $errorMessage\n" +
                    "📊 Procesados hasta el momento: $totalSuccess exitosos, $totalFailed fallidos"
        )
    }
}