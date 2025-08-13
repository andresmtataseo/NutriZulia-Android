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
import kotlinx.coroutines.coroutineScope
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
        android.util.Log.d("SyncCollectionBatch", "=== INICIANDO SINCRONIZACIÓN BATCH ===")
        val results = mutableListOf<TableSyncResult>()
        var overallSuccess = true
        var totalSuccess = 0
        var totalFailed = 0

        try {
            // Paso 1: Sincronizar tablas independientes secuencialmente
            android.util.Log.d("SyncCollectionBatch", "Iniciando sincronización secuencial de tablas independientes")
            
            android.util.Log.d("SyncCollectionBatch", "Sincronizando representantes...")
            val representantesResult = syncRepresentantes()
            results.add(representantesResult)
            if (!representantesResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en representantes")
            }
            
            android.util.Log.d("SyncCollectionBatch", "Sincronizando pacientes...")
            val pacientesResult = syncPacientes()
            results.add(pacientesResult)
            if (!pacientesResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en pacientes")
            }

            // Paso 2: Sincronizar tablas dependientes
            android.util.Log.d("SyncCollectionBatch", "Sincronizando pacientes-representantes...")
            val pacientesRepresentantesResult = syncPacientesRepresentantes()
            results.add(pacientesRepresentantesResult)
            if (!pacientesRepresentantesResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en pacientes-representantes")
            }

            // Paso 3: Sincronizar datos clínicos secuencialmente para evitar violaciones de llave foránea
            android.util.Log.d("SyncCollectionBatch", "Iniciando sincronización secuencial de datos clínicos")
            
            // Paso 3.1: Sincronizar consultas primero (base para otros datos)
            android.util.Log.d("SyncCollectionBatch", "Sincronizando consultas...")
            val consultasResult = syncConsultas()
            results.add(consultasResult)
            if (!consultasResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en consultas")
            }
            
            // Paso 3.2: Sincronizar detalles (independientes entre sí)
            android.util.Log.d("SyncCollectionBatch", "Sincronizando detalles antropométricos...")
            val detallesAntropometricosResult = syncDetallesAntropometricos()
            results.add(detallesAntropometricosResult)
            if (!detallesAntropometricosResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en detalles antropométricos")
            }
            
            android.util.Log.d("SyncCollectionBatch", "Sincronizando detalles metabólicos...")
            val detallesMetabolicosResult = syncDetallesMetabolicos()
            results.add(detallesMetabolicosResult)
            if (!detallesMetabolicosResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en detalles metabólicos")
            }
            
            android.util.Log.d("SyncCollectionBatch", "Sincronizando detalles obstetricia...")
            val detallesObstetriciaResult = syncDetallesObstetricia()
            results.add(detallesObstetriciaResult)
            if (!detallesObstetriciaResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en detalles obstetricia")
            }
            
            android.util.Log.d("SyncCollectionBatch", "Sincronizando detalles pediátricos...")
            val detallesPediatricosResult = syncDetallesPediatricos()
            results.add(detallesPediatricosResult)
            if (!detallesPediatricosResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en detalles pediátricos")
            }
            
            android.util.Log.d("SyncCollectionBatch", "Sincronizando detalles vitales...")
            val detallesVitalesResult = syncDetallesVitales()
            results.add(detallesVitalesResult)
            if (!detallesVitalesResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en detalles vitales")
            }
            
            // Paso 3.3: Sincronizar diagnósticos
            android.util.Log.d("SyncCollectionBatch", "Sincronizando diagnósticos...")
            val diagnosticosResult = syncDiagnosticos()
            results.add(diagnosticosResult)
            if (!diagnosticosResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en diagnósticos")
            }
            
            // Paso 3.4: Sincronizar evaluaciones antropométricas AL FINAL (depende de detalles antropométricos)
            android.util.Log.d("SyncCollectionBatch", "Sincronizando evaluaciones antropométricas...")
            val evaluacionesAntropometricasResult = syncEvaluacionesAntropometricas()
            results.add(evaluacionesAntropometricasResult)
            if (!evaluacionesAntropometricasResult.isSuccess) {
                overallSuccess = false
                android.util.Log.w("SyncCollectionBatch", "Error en evaluaciones antropométricas")
            }

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

        android.util.Log.d("SyncCollectionBatch", "=== FINALIZANDO SINCRONIZACIÓN BATCH ===\nÉxito general: $overallSuccess\nTotal exitosos: $totalSuccess\nTotal fallidos: $totalFailed\nTablas procesadas: ${results.size}")
        
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

    private suspend fun syncRepresentantes(): TableSyncResult {
        return try {
            when (val result = representanteRepository.sincronizarRepresentantesBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Representantes",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Representantes",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Representantes",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Representantes",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Representantes",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncConsultas(): TableSyncResult {
        return try {
            android.util.Log.d("SyncCollectionBatch", "Iniciando sincronización de consultas")
            when (val result = consultaRepository.sincronizarConsultasBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Consultas",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Consultas",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Consultas",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Consultas",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Consultas",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesAntropometricos(): TableSyncResult {
        return try {
            when (val result = antropometricoRepository.sincronizarDetallesAntropometricosBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Detalles Antropométricos",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Detalles Antropométricos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Detalles Antropométricos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Detalles Antropométricos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Detalles Antropométricos",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesMetabolicos(): TableSyncResult {
        return try {
            when (val result = metabolicoRepository.sincronizarDetallesMetabolicosBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Detalles Metabólicos",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Detalles Metabólicos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Detalles Metabólicos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Detalles Metabólicos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Detalles Metabólicos",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesObstetricia(): TableSyncResult {
        return try {
            when (val result = obstetriciaRepository.sincronizarDetallesObstetriciaBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Detalles Obstetricia",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Detalles Obstetricia",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Detalles Obstetricia",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Detalles Obstetricia",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Detalles Obstetricia",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesPediatricos(): TableSyncResult {
        return try {
            when (val result = pediatricoRepository.sincronizarDetallesPediatricosBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Detalles Pediátricos",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Detalles Pediátricos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Detalles Pediátricos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Detalles Pediátricos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Detalles Pediátricos",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesVitales(): TableSyncResult {
        return try {
            when (val result = vitalRepository.sincronizarDetallesVitalesBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Detalles Vitales",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Detalles Vitales",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Detalles Vitales",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Detalles Vitales",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Detalles Vitales",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDiagnosticos(): TableSyncResult {
        return try {
            when (val result = diagnosticoRepository.sincronizarDiagnosticosBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Diagnósticos",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Diagnósticos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Diagnósticos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Diagnósticos",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Diagnósticos",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncEvaluacionesAntropometricas(): TableSyncResult {
        return try {
            when (val result = evaluacionRepository.sincronizarEvaluacionesAntropometricasBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "Evaluaciones Antropométricas",
                        isSuccess = batchResult.isCompleteSuccess || batchResult.hasPartialSuccess,
                        successCount = batchResult.getSuccessCount(),
                        failedCount = batchResult.getFailureCount(),
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "Evaluaciones Antropométricas",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "Evaluaciones Antropométricas",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "Evaluaciones Antropométricas",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "Evaluaciones Antropométricas",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncPacientesRepresentantes(): TableSyncResult {
        return try {
            when (val result = pacienteRepresentanteRepository.sincronizarPacientesRepresentantesBatch()) {
                is SyncResult.Success -> {
                    val batchResult = result.data
                    TableSyncResult(
                        tableName = "PacientesRepresentantes",
                        isSuccess = true,
                        successCount = batchResult.successfulUuids.size,
                        failedCount = batchResult.failedUuids.size,
                        errorMessage = if (batchResult.failedUuids.isNotEmpty()) batchResult.getFailureMessage() else null,
                        details = batchResult
                    )
                }
                is SyncResult.BusinessError -> {
                    TableSyncResult(
                        tableName = "PacientesRepresentantes",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = "Error de negocio: ${result.message}"
                    )
                }
                is SyncResult.NetworkError -> {
                    TableSyncResult(
                        tableName = "PacientesRepresentantes",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = "Error de red: ${result.message}"
                    )
                }
                is SyncResult.UnknownError -> {
                    TableSyncResult(
                        tableName = "PacientesRepresentantes",
                        isSuccess = false,
                        successCount = 0,
                        failedCount = 0,
                        errorMessage = "Error desconocido: ${result.exception.message ?: "Error desconocido"}"
                    )
                }
            }
        } catch (e: Exception) {
            TableSyncResult(
                tableName = "PacientesRepresentantes",
                isSuccess = false,
                successCount = 0,
                failedCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }


}