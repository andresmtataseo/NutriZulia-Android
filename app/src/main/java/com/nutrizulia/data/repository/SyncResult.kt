package com.nutrizulia.data.repository

/**
 * Resultado de una operación de sincronización
 */
sealed class SyncResult {
    /**
     * Sincronización exitosa
     * @param pushedCount Número de registros enviados al servidor
     * @param pulledCount Número de registros recibidos del servidor
     * @param message Mensaje descriptivo del resultado
     */
    data class Success(
        val pushedCount: Int,
        val pulledCount: Int,
        val message: String = "Sincronización completada exitosamente"
    ) : SyncResult()

    /**
     * Error durante la sincronización
     * @param exception Excepción que causó el error
     * @param message Mensaje descriptivo del error
     */
    data class Error(
        val exception: Throwable,
        val message: String = "Error durante la sincronización"
    ) : SyncResult()

    /**
     * Sincronización en progreso
     * @param message Mensaje descriptivo del progreso
     */
    data class Loading(
        val message: String = "Sincronizando datos..."
    ) : SyncResult()
}