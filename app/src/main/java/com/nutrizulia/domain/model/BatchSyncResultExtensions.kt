package com.nutrizulia.domain.model

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.collection.BatchSyncResponseDto
import retrofit2.Response

/**
 * Convierte una respuesta de la API de sincronización por lotes a BatchSyncResult
 */
fun Response<ApiResponseDto<BatchSyncResponseDto>>.toBatchSyncResult(): SyncResult<BatchSyncResult> {
    return if (isSuccessful) {
        val body = body()
        if (body != null) {
            when (body.status) {
                in 200..299 -> {
                    val batchData = body.data ?: BatchSyncResponseDto()
                    val batchResult = BatchSyncResult(
                        successfulUuids = batchData.success,
                        failedUuids = batchData.failed
                    )
                    // Verificar si hay errores en el batch
                    if (batchResult.failedUuids.isNotEmpty()) {
                        if (batchResult.successfulUuids.isNotEmpty()) {
                            // Éxito parcial
                            SyncResult.Success(
                                batchResult, 
                                "Sincronización parcial: ${batchResult.successfulUuids.size} exitosos, ${batchResult.failedUuids.size} fallidos"
                            )
                        } else {
                            // Todos fallaron
                            SyncResult.BusinessError(
                                body.status,
                                body.message ?: "Error en la sincronización",
                                body.errors
                            )
                        }
                    } else {
                        // Todos exitosos
                        SyncResult.Success(batchResult, body.message)
                    }
                }
                in 400..499 -> SyncResult.BusinessError(body.status, body.message, body.errors)
                else -> SyncResult.NetworkError(body.status, body.message)
            }
        } else {
            SyncResult.NetworkError(code(), "Respuesta vacía del servidor")
        }
    } else {
        SyncResult.NetworkError(code(), message())
    }
}

/**
 * Convierte una respuesta de la API de sincronización por lotes a BatchSyncResult con transformación personalizada
 */
inline fun <R> Response<ApiResponseDto<BatchSyncResponseDto>>.toBatchSyncResult(
    transform: (BatchSyncResult) -> SyncResult<R>
): SyncResult<R> {
    return if (isSuccessful) {
        val body = body()
        if (body != null) {
            when (body.status) {
                in 200..299 -> {
                    val batchData = body.data ?: BatchSyncResponseDto()
                    val batchResult = BatchSyncResult(
                        successfulUuids = batchData.success,
                        failedUuids = batchData.failed
                    )
                    transform(batchResult)
                }
                in 400..499 -> SyncResult.BusinessError(body.status, body.message, body.errors)
                else -> SyncResult.NetworkError(body.status, body.message)
            }
        } else {
            SyncResult.NetworkError(code(), "Respuesta vacía del servidor")
        }
    } else {
        SyncResult.NetworkError(code(), message())
    }
}