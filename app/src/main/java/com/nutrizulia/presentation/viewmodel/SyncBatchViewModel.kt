package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.SyncCollectionBatch
import com.nutrizulia.domain.usecase.dashboard.GetPendingRecordsByEntityUseCase
import com.nutrizulia.domain.usecase.dashboard.GetTotalPendingRecordsUseCase
import com.nutrizulia.domain.usecase.dashboard.PendingRecordsByEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncBatchViewModel @Inject constructor(
    private val syncCollectionBatch: SyncCollectionBatch,
    private val getPendingRecordsByEntityUseCase: GetPendingRecordsByEntityUseCase,
    private val getTotalPendingRecordsUseCase: GetTotalPendingRecordsUseCase
) : ViewModel() {

    // StateFlow para el total de registros pendientes
    private val _totalPendingRecords = MutableStateFlow(0)
    val totalPendingRecords: StateFlow<Int> = _totalPendingRecords.asStateFlow()
    
    // StateFlow para los contadores por entidad (mantenido para compatibilidad con sincronización)
    private val _pendingRecords = MutableStateFlow(PendingRecordsByEntity(0, 0, 0, 0, 0, 0))
    val pendingRecords: StateFlow<PendingRecordsByEntity> = _pendingRecords.asStateFlow()

    // Callbacks para manejar los diferentes estados de la sincronización
    var onSyncStart: ((message: String) -> Unit)? = null
    var onSyncSuccess: ((successCount: Int, totalProcessed: Int, message: String, detailedReport: String) -> Unit)? = null
    var onSyncPartialSuccess: ((successCount: Int, totalProcessed: Int, failureCount: Int, message: String, detailedReport: String) -> Unit)? = null
    var onSyncError: ((message: String, details: String?) -> Unit)? = null

    init {
        loadPendingRecords()
    }

    /**
     * Carga los contadores de registros pendientes
     */
    fun loadPendingRecords() {
        viewModelScope.launch {
            try {
                // Cargar total de registros pendientes
                val totalRecords = getTotalPendingRecordsUseCase()
                _totalPendingRecords.value = totalRecords
                
                // Cargar registros por entidad (para sincronización)
                val records = getPendingRecordsByEntityUseCase()
                _pendingRecords.value = records
            } catch (e: Exception) {
                _totalPendingRecords.value = 0
                _pendingRecords.value = PendingRecordsByEntity(0, 0, 0, 0, 0, 0)
            }
        }
    }

    /**
     * Inicia la sincronización siguiendo todos los requerimientos del usuario:
     * - Mostrar mensaje de inicio
     * - Procesar tablas en orden específico
     * - Manejar success/failed por UUID
     * - Mostrar reporte detallado por tabla y lote
     * - Manejar errores de red apropiadamente
     */
    fun iniciarSincronizacion() {
        viewModelScope.launch {
            try {
                // Paso 1: Mostrar mensaje de inicio
                onSyncStart?.invoke("Iniciando sincronización con el servidor...")
                
                // Paso 2: Ejecutar sincronización por lotes
                val results = syncCollectionBatch.invoke()
                
                // Paso 3: Analizar resultados y generar reporte
                val totalSuccess = results.totalSuccessCount
                val totalFailure = results.totalFailedCount
                val totalProcessed = results.getTotalProcessed()
                
                // Paso 4: Generar reporte detallado
                val detailedReport = generateDetailedReport(results)
                
                // Paso 5: Determinar tipo de resultado y notificar
                when {
                    totalProcessed == 0 -> {
                        onSyncSuccess?.invoke(
                            0, 0,
                            "No hay registros pendientes de sincronización",
                            "Todas las tablas están actualizadas."
                        )
                    }
                    totalFailure == 0 && totalSuccess > 0 -> {
                        // Éxito completo
                        onSyncSuccess?.invoke(
                            totalSuccess, totalProcessed,
                            "Sincronización completada exitosamente",
                            detailedReport
                        )
                        // Recargar contadores después del éxito
                        loadPendingRecords()
                    }
                    totalSuccess > 0 && totalFailure > 0 -> {
                        // Éxito parcial
                        onSyncPartialSuccess?.invoke(
                            totalSuccess, totalProcessed, totalFailure,
                            "Sincronización parcial completada",
                            detailedReport
                        )
                        // Recargar contadores después del éxito parcial
                        loadPendingRecords()
                    }
                    else -> {
                        // Error completo
                        val errorInfo = analyzeErrors(results)
                        onSyncError?.invoke(errorInfo.first, errorInfo.second)
                    }
                }
                
            } catch (e: Exception) {
                onSyncError?.invoke(
                    "Error inesperado durante la sincronización", 
                    e.message ?: "Error desconocido"
                )
            }
        }
    }
    
    /**
     * Genera un reporte detallado por tabla y lote según los requerimientos
     */
    private fun generateDetailedReport(results: SyncCollectionBatch.CollectionBatchSyncResults): String {
        val report = StringBuilder()
        report.appendLine("📊 REPORTE DE SINCRONIZACIÓN POR TABLA:")
        report.appendLine()
        
        results.tableResults.forEach { tableResult ->
            when {
                tableResult.successCount == 0 && tableResult.failedCount == 0 -> {
                    report.appendLine("⏭️ ${tableResult.tableName}: Sin registros pendientes")
                }
                tableResult.isSuccess && tableResult.failedCount == 0 -> {
                    report.appendLine("✅ ${tableResult.tableName}: ${tableResult.successCount} registros sincronizados")
                }
                tableResult.successCount > 0 && tableResult.failedCount > 0 -> {
                    val total = tableResult.successCount + tableResult.failedCount
                    report.appendLine("⚠️ ${tableResult.tableName}: ${tableResult.successCount}/$total sincronizados")
                    tableResult.errorMessage?.let {
                        report.appendLine("   Motivo: $it")
                    }
                }
                !tableResult.isSuccess -> {
                    report.appendLine("❌ ${tableResult.tableName}: ${tableResult.failedCount} registros fallaron")
                    tableResult.errorMessage?.let {
                        report.appendLine("   Motivo: $it")
                    }
                }
                else -> {
                    report.appendLine("❓ ${tableResult.tableName}: Error desconocido")
                }
            }
        }
        
        report.appendLine()
        report.appendLine("📈 RESUMEN GENERAL:")
        report.appendLine("• Total procesados: ${results.totalSuccessCount + results.totalFailedCount}")
        report.appendLine("• Exitosos: ${results.totalSuccessCount}")
        report.appendLine("• Fallidos: ${results.totalFailedCount}")
        
        val tablesWithSuccess = results.tableResults.filter { it.isSuccess && it.successCount > 0 }.map { it.tableName }
        if (tablesWithSuccess.isNotEmpty()) {
            report.appendLine("• Tablas con éxito: ${tablesWithSuccess.joinToString(", ")}")
        }
        
        val tablesWithErrors = results.getTablesWithErrors().map { it.tableName }
        if (tablesWithErrors.isNotEmpty()) {
            report.appendLine("• Tablas con errores: ${tablesWithErrors.joinToString(", ")}")
        }
        
        return report.toString()
    }
    
    /**
     * Analiza los errores para determinar el tipo principal y generar mensaje apropiado
     */
    private fun analyzeErrors(results: SyncCollectionBatch.CollectionBatchSyncResults): Pair<String, String> {
        var hasNetworkError = false
        var hasConnectivityError = false
        var hasBusinessError = false
        val errorDetails = mutableListOf<String>()
        
        results.tableResults.filter { !it.isSuccess }.forEach { tableResult ->
            val errorMessage = tableResult.errorMessage ?: "Error desconocido"
            
            when {
                errorMessage.contains("network", ignoreCase = true) || 
                errorMessage.contains("timeout", ignoreCase = true) -> {
                    hasNetworkError = true
                    errorDetails.add("${tableResult.tableName}: Error de red")
                }
                errorMessage.contains("connection", ignoreCase = true) || 
                errorMessage.contains("conectividad", ignoreCase = true) -> {
                    hasConnectivityError = true
                    errorDetails.add("${tableResult.tableName}: Sin conectividad")
                }
                errorMessage.contains("validation", ignoreCase = true) || 
                errorMessage.contains("validación", ignoreCase = true) -> {
                    hasBusinessError = true
                    errorDetails.add("${tableResult.tableName}: Error de validación")
                }
                else -> {
                    errorDetails.add("${tableResult.tableName}: $errorMessage")
                }
            }
        }
        
        val mainMessage = when {
            hasConnectivityError -> "Error de conectividad"
            hasNetworkError -> "Error de comunicación con el servidor"
            hasBusinessError -> "Errores de validación en los datos"
            else -> "Error en la sincronización"
        }
        
        val details = when {
            hasConnectivityError -> "No se puede conectar al servidor. Verifique su conexión a internet y que el servidor esté disponible."
            hasNetworkError -> "Problemas de comunicación con el servidor. Intente nuevamente más tarde."
            else -> errorDetails.take(3).joinToString("\n") + if (errorDetails.size > 3) "\n..." else ""
        }
        
        return Pair(mainMessage, details)
    }
}