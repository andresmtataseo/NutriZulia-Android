package com.nutrizulia.domain.model

sealed class SyncResult<out T> {
    data class Success<T>(val data: T, val message: String) : SyncResult<T>()
    data class BusinessError(val status: Int, val message: String, val errors: Map<String, String>? = null) : SyncResult<Nothing>()
    data class NetworkError(val code: Int, val message: String) : SyncResult<Nothing>()
    data class UnknownError(val exception: Throwable) : SyncResult<Nothing>()
}