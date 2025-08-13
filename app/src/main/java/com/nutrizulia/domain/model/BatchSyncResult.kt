package com.nutrizulia.domain.model

/**
 * Resultado de sincronización por lotes que maneja tanto registros exitosos como fallidos
 */
data class BatchSyncResult(
    val successfulUuids: List<String> = emptyList(),
    val failedUuids: Map<String, String> = emptyMap(), // UUID -> motivo del fallo
    val totalProcessed: Int = successfulUuids.size + failedUuids.size,
    val hasPartialSuccess: Boolean = successfulUuids.isNotEmpty() && failedUuids.isNotEmpty(),
    val isCompleteSuccess: Boolean = successfulUuids.isNotEmpty() && failedUuids.isEmpty(),
    val isCompleteFailure: Boolean = successfulUuids.isEmpty() && failedUuids.isNotEmpty()
) {
    fun getSuccessCount(): Int = successfulUuids.size
    fun getFailureCount(): Int = failedUuids.size
    
    fun getSuccessMessage(): String {
        return when {
            isCompleteSuccess -> "Todos los registros (${getSuccessCount()}) se sincronizaron correctamente"
            hasPartialSuccess -> "${getSuccessCount()} de ${totalProcessed} registros sincronizados correctamente"
            else -> "No se pudo sincronizar ningún registro"
        }
    }
    
    fun getFailureMessage(): String {
        return if (failedUuids.isNotEmpty()) {
            "${getFailureCount()} registros fallaron: ${failedUuids.values.take(3).joinToString(", ")}${if (failedUuids.size > 3) "..." else ""}"
        } else {
            ""
        }
    }
}