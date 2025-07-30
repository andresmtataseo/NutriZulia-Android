package com.nutrizulia.util

import java.time.LocalDateTime

/**
 * Resultado de una operación de sincronización.
 * Contiene información detallada sobre el éxito o fallo del proceso.
 */
data class SyncResult(
    val success: Boolean,
    val message: String,
    val totalInsertados: Int = 0,
    val totalActualizados: Int = 0,
    val totalEnviados: Int = 0,
    val syncTimestamp: LocalDateTime? = null,
    val error: Throwable? = null
) {
    companion object {
        fun success(
            insertados: Int = 0,
            actualizados: Int = 0,
            enviados: Int = 0,
            timestamp: LocalDateTime,
            message: String = "Sincronización completada exitosamente"
        ) = SyncResult(
            success = true,
            message = message,
            totalInsertados = insertados,
            totalActualizados = actualizados,
            totalEnviados = enviados,
            syncTimestamp = timestamp
        )

        fun failure(
            message: String,
            error: Throwable? = null
        ) = SyncResult(
            success = false,
            message = message,
            error = error
        )
    }
}