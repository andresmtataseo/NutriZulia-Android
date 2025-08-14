package com.nutrizulia.domain.usecase

import android.util.Log
import com.nutrizulia.data.repository.collection.*
import com.nutrizulia.domain.model.SyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Caso de uso para sincronización completa (full sync) de todas las colecciones
 * Recupera todos los datos del usuario desde el backend para restaurar datos locales
 */
@Singleton
class FullSyncCollections @Inject constructor(
    private val representanteRepository: RepresentanteRepository,
    private val pacienteRepository: PacienteRepository,
    private val pacienteRepresentanteRepository: PacienteRepresentanteRepository,
    private val consultaRepository: ConsultaRepository,
    private val detalleAntropometricoRepository: DetalleAntropometricoRepository,
    private val detalleMetabolicoRepository: DetalleMetabolicoRepository,
    private val detalleObstetriciaRepository: DetalleObstetriciaRepository,
    private val detallePediatricoRepository: DetallePediatricoRepository,
    private val detalleVitalRepository: DetalleVitalRepository,
    private val diagnosticoRepository: DiagnosticoRepository,
    private val evaluacionAntropometricaRepository: EvaluacionAntropometricaRepository,
    private val actividadRepository: ActividadRepository
) {

    /**
     * Resultado de sincronización completa para una tabla específica
     */
    data class TableFullSyncResult(
        val tableName: String,
        val isSuccess: Boolean,
        val recordsCount: Int,
        val errorMessage: String? = null
    )

    /**
     * Resultado completo de la sincronización completa
     */
    data class FullSyncResults(
        val tableResults: List<TableFullSyncResult>,
        val overallSuccess: Boolean,
        val totalRecordsRestored: Int,
        val tablesProcessed: Int,
        val tablesSuccessful: Int,
        val tablesFailed: Int,
        val summary: String
    ) {
        fun getTablesWithErrors(): List<TableFullSyncResult> {
            return tableResults.filter { !it.isSuccess }
        }
    }

    /**
     * Ejecuta la sincronización completa de todas las colecciones
     * Mantiene el orden correcto para preservar integridad referencial
     * @param onProgress Callback para reportar progreso (tabla actual, total de tablas)
     * @return Resultado detallado de la sincronización completa
     */
    suspend fun invoke(
        onProgress: ((currentTable: String, currentIndex: Int, totalTables: Int) -> Unit)? = null
    ): FullSyncResults = withContext(Dispatchers.IO) {
        Log.d("FullSyncCollections", "=== INICIANDO SINCRONIZACIÓN COMPLETA ===")
        
        val results = mutableListOf<TableFullSyncResult>()
        var overallSuccess = true
        var totalRecords = 0
        val totalTables = 12
        var currentTableIndex = 0

        try {
            // Paso 1: Sincronizar tablas independientes
            Log.d("FullSyncCollections", "Iniciando sincronización de tablas independientes")
            
            // 1. Representantes
            currentTableIndex++
            onProgress?.invoke("Representantes", currentTableIndex, totalTables)
            val representantesResult = syncRepresentantesComplete()
            results.add(representantesResult)
            if (!representantesResult.isSuccess) overallSuccess = false
            totalRecords += representantesResult.recordsCount
            
            // 2. Pacientes
            currentTableIndex++
            onProgress?.invoke("Pacientes", currentTableIndex, totalTables)
            val pacientesResult = syncPacientesComplete()
            results.add(pacientesResult)
            if (!pacientesResult.isSuccess) overallSuccess = false
            totalRecords += pacientesResult.recordsCount
            
            // 3. Actividades
            currentTableIndex++
            onProgress?.invoke("Actividades", currentTableIndex, totalTables)
            val actividadesResult = syncActividadesComplete()
            results.add(actividadesResult)
            if (!actividadesResult.isSuccess) overallSuccess = false
            totalRecords += actividadesResult.recordsCount

            // Paso 2: Sincronizar tablas dependientes
            Log.d("FullSyncCollections", "Iniciando sincronización de tablas dependientes")
            
            // 4. Pacientes-Representantes (depende de pacientes y representantes)
            currentTableIndex++
            onProgress?.invoke("Pacientes-Representantes", currentTableIndex, totalTables)
            val pacientesRepresentantesResult = syncPacientesRepresentantesComplete()
            results.add(pacientesRepresentantesResult)
            if (!pacientesRepresentantesResult.isSuccess) overallSuccess = false
            totalRecords += pacientesRepresentantesResult.recordsCount
            
            // 5. Consultas (depende de pacientes)
            currentTableIndex++
            onProgress?.invoke("Consultas", currentTableIndex, totalTables)
            val consultasResult = syncConsultasComplete()
            results.add(consultasResult)
            if (!consultasResult.isSuccess) overallSuccess = false
            totalRecords += consultasResult.recordsCount

            // Paso 3: Sincronizar detalles clínicos (dependen de consultas)
            Log.d("FullSyncCollections", "Iniciando sincronización de detalles clínicos")
            
            // 6. Detalles Antropométricos
            currentTableIndex++
            onProgress?.invoke("Detalles Antropométricos", currentTableIndex, totalTables)
            val detallesAntropometricosResult = syncDetallesAntropometricosComplete()
            results.add(detallesAntropometricosResult)
            if (!detallesAntropometricosResult.isSuccess) overallSuccess = false
            totalRecords += detallesAntropometricosResult.recordsCount
            
            // 7. Detalles Metabólicos
            currentTableIndex++
            onProgress?.invoke("Detalles Metabólicos", currentTableIndex, totalTables)
            val detallesMetabolicosResult = syncDetallesMetabolicosComplete()
            results.add(detallesMetabolicosResult)
            if (!detallesMetabolicosResult.isSuccess) overallSuccess = false
            totalRecords += detallesMetabolicosResult.recordsCount
            
            // 8. Detalles Obstétricos
            currentTableIndex++
            onProgress?.invoke("Detalles Obstétricos", currentTableIndex, totalTables)
            val detallesObstetriciaResult = syncDetallesObstetriciaComplete()
            results.add(detallesObstetriciaResult)
            if (!detallesObstetriciaResult.isSuccess) overallSuccess = false
            totalRecords += detallesObstetriciaResult.recordsCount
            
            // 9. Detalles Pediátricos
            currentTableIndex++
            onProgress?.invoke("Detalles Pediátricos", currentTableIndex, totalTables)
            val detallesPediatricosResult = syncDetallesPediatricosComplete()
            results.add(detallesPediatricosResult)
            if (!detallesPediatricosResult.isSuccess) overallSuccess = false
            totalRecords += detallesPediatricosResult.recordsCount
            
            // 10. Detalles Vitales
            currentTableIndex++
            onProgress?.invoke("Detalles Vitales", currentTableIndex, totalTables)
            val detallesVitalesResult = syncDetallesVitalesComplete()
            results.add(detallesVitalesResult)
            if (!detallesVitalesResult.isSuccess) overallSuccess = false
            totalRecords += detallesVitalesResult.recordsCount
            
            // 11. Diagnósticos
            currentTableIndex++
            onProgress?.invoke("Diagnósticos", currentTableIndex, totalTables)
            val diagnosticosResult = syncDiagnosticosComplete()
            results.add(diagnosticosResult)
            if (!diagnosticosResult.isSuccess) overallSuccess = false
            totalRecords += diagnosticosResult.recordsCount
            
            // 12. Evaluaciones Antropométricas (depende de detalles antropométricos)
            currentTableIndex++
            onProgress?.invoke("Evaluaciones Antropométricas", currentTableIndex, totalTables)
            val evaluacionesAntropometricasResult = syncEvaluacionesAntropometricasComplete()
            results.add(evaluacionesAntropometricasResult)
            if (!evaluacionesAntropometricasResult.isSuccess) overallSuccess = false
            totalRecords += evaluacionesAntropometricasResult.recordsCount

        } catch (e: Exception) {
            Log.e("FullSyncCollections", "Error general en sincronización completa", e)
            overallSuccess = false
            results.add(
                TableFullSyncResult(
                    tableName = "Sistema",
                    isSuccess = false,
                    recordsCount = 0,
                    errorMessage = "Error general: ${e.message}"
                )
            )
        }

        val tablesSuccessful = results.count { it.isSuccess }
        val tablesFailed = results.count { !it.isSuccess }
        
        Log.d("FullSyncCollections", "=== FINALIZANDO SINCRONIZACIÓN COMPLETA ===\nÉxito general: $overallSuccess\nTotal registros: $totalRecords\nTablas exitosas: $tablesSuccessful\nTablas fallidas: $tablesFailed")
        
        return@withContext FullSyncResults(
            tableResults = results,
            overallSuccess = overallSuccess,
            totalRecordsRestored = totalRecords,
            tablesProcessed = results.size,
            tablesSuccessful = tablesSuccessful,
            tablesFailed = tablesFailed,
            summary = generateSummary(results, overallSuccess, totalRecords, tablesSuccessful, tablesFailed)
        )
    }

    private fun generateSummary(
        results: List<TableFullSyncResult>,
        overallSuccess: Boolean,
        totalRecords: Int,
        tablesSuccessful: Int,
        tablesFailed: Int
    ): String {
        val tablesWithErrors = results.filter { !it.isSuccess }

        return buildString {
            if (overallSuccess) {
                append("✅ Recuperación de datos completada exitosamente\n")
            } else {
                append("⚠️ Recuperación de datos completada con errores\n")
            }

            append("📊 Resumen: $totalRecords registros recuperados\n")
            append("📋 Tablas: $tablesSuccessful exitosas, $tablesFailed fallidas\n")

            if (tablesWithErrors.isNotEmpty()) {
                append("\n❌ Tablas con errores:\n")
                tablesWithErrors.forEach { table ->
                    append("• ${table.tableName}: ${table.errorMessage ?: "Error desconocido"}\n")
                }
            }

            append("\n📋 Detalle por tabla:\n")
            results.forEach { table ->
                val status = if (table.isSuccess) "✅" else "❌"
                append("$status ${table.tableName}: ${table.recordsCount} registros\n")
            }
        }
    }

    // Métodos privados para sincronización completa de cada tabla
    private suspend fun syncRepresentantesComplete(): TableFullSyncResult {
        return try {
            when (val result = representanteRepository.fullSyncRepresentantes()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Representantes",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Representantes",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Representantes",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Representantes",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Representantes",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncPacientesComplete(): TableFullSyncResult {
        return try {
            when (val result = pacienteRepository.fullSyncPacientes()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Pacientes",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Pacientes",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Pacientes",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Pacientes",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Pacientes",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncActividadesComplete(): TableFullSyncResult {
        return try {
            when (val result = actividadRepository.fullSyncActividades()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Actividades",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Actividades",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Actividades",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Actividades",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Actividades",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncPacientesRepresentantesComplete(): TableFullSyncResult {
        return try {
            when (val result = pacienteRepresentanteRepository.fullSyncPacientesRepresentantes()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Pacientes-Representantes",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Pacientes-Representantes",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Pacientes-Representantes",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Pacientes-Representantes",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Pacientes-Representantes",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncConsultasComplete(): TableFullSyncResult {
        return try {
            when (val result = consultaRepository.fullSyncConsultas()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Consultas",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Consultas",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Consultas",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Consultas",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Consultas",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesAntropometricosComplete(): TableFullSyncResult {
        return try {
            when (val result = detalleAntropometricoRepository.fullSyncDetallesAntropometricos()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Detalles Antropométricos",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Antropométricos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Antropométricos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Antropométricos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Detalles Antropométricos",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesMetabolicosComplete(): TableFullSyncResult {
        return try {
            when (val result = detalleMetabolicoRepository.fullSyncDetallesMetabolicos()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Detalles Metabólicos",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Metabólicos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Metabólicos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Metabólicos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Detalles Metabólicos",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesObstetriciaComplete(): TableFullSyncResult {
        return try {
            when (val result = detalleObstetriciaRepository.fullSyncDetallesObstetricia()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Detalles Obstétricos",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Obstétricos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Obstétricos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Obstétricos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Detalles Obstétricos",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesPediatricosComplete(): TableFullSyncResult {
        return try {
            when (val result = detallePediatricoRepository.fullSyncDetallesPediatricos()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Detalles Pediátricos",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Pediátricos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Pediátricos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Pediátricos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Detalles Pediátricos",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDetallesVitalesComplete(): TableFullSyncResult {
        return try {
            when (val result = detalleVitalRepository.fullSyncDetallesVitales()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Detalles Vitales",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Vitales",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Vitales",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Detalles Vitales",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Detalles Vitales",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncDiagnosticosComplete(): TableFullSyncResult {
        return try {
            when (val result = diagnosticoRepository.fullSyncDiagnosticos()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Diagnósticos",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Diagnósticos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Diagnósticos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Diagnósticos",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Diagnósticos",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }

    private suspend fun syncEvaluacionesAntropometricasComplete(): TableFullSyncResult {
        return try {
            when (val result = evaluacionAntropometricaRepository.fullSyncEvaluacionesAntropometricas()) {
                is SyncResult.Success -> {
                    TableFullSyncResult(
                        tableName = "Evaluaciones Antropométricas",
                        isSuccess = true,
                        recordsCount = result.data
                    )
                }
                is SyncResult.BusinessError -> {
                    TableFullSyncResult(
                        tableName = "Evaluaciones Antropométricas",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.NetworkError -> {
                    TableFullSyncResult(
                        tableName = "Evaluaciones Antropométricas",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.message
                    )
                }
                is SyncResult.UnknownError -> {
                    TableFullSyncResult(
                        tableName = "Evaluaciones Antropométricas",
                        isSuccess = false,
                        recordsCount = 0,
                        errorMessage = result.exception.message ?: "Error desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            TableFullSyncResult(
                tableName = "Evaluaciones Antropométricas",
                isSuccess = false,
                recordsCount = 0,
                errorMessage = "Error inesperado: ${e.message}"
            )
        }
    }
}