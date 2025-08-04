package com.nutrizulia.domain.model

/**
 * Ejecuta una acción cuando el resultado es exitoso
 */
inline fun <T> SyncResult<T>.onSuccess(action: (data: T, message: String) -> Unit): SyncResult<T> {
    if (this is SyncResult.Success) {
        action(data, message)
    }
    return this
}

/**
 * Ejecuta una acción cuando hay un error de negocio
 */
inline fun <T> SyncResult<T>.onBusinessError(action: (status: Int, message: String, errors: Map<String, String>?) -> Unit): SyncResult<T> {
    if (this is SyncResult.BusinessError) {
        action(status, message, errors)
    }
    return this
}

/**
 * Ejecuta una acción específicamente cuando hay un error 409 (Conflict - Violación de integridad)
 */
inline fun <T> SyncResult<T>.onConflictError(action: (message: String, errors: Map<String, String>?) -> Unit): SyncResult<T> {
    if (this is SyncResult.BusinessError && status == 409) {
        action(message, errors)
    }
    return this
}

/**
 * Ejecuta una acción cuando hay un error de red
 */
inline fun <T> SyncResult<T>.onNetworkError(action: (code: Int, message: String) -> Unit): SyncResult<T> {
    if (this is SyncResult.NetworkError) {
        action(code, message)
    }
    return this
}

/**
 * Ejecuta una acción cuando hay un error desconocido
 */
inline fun <T> SyncResult<T>.onUnknownError(action: (exception: Throwable) -> Unit): SyncResult<T> {
    if (this is SyncResult.UnknownError) {
        action(exception)
    }
    return this
}

/**
 * Ejecuta una acción para cualquier tipo de error
 */
inline fun <T> SyncResult<T>.onError(action: (errorMessage: String) -> Unit): SyncResult<T> {
    when (this) {
        is SyncResult.BusinessError -> action(message)
        is SyncResult.NetworkError -> action(message)
        is SyncResult.UnknownError -> action(exception.message ?: "Error desconocido")
        is SyncResult.Success -> { /* No hacer nada */ }
    }
    return this
}

/**
 * Retorna true si el resultado es exitoso
 */
fun <T> SyncResult<T>.isSuccess(): Boolean = this is SyncResult.Success

/**
 * Retorna true si el resultado es un error
 */
fun <T> SyncResult<T>.isError(): Boolean = this !is SyncResult.Success

/**
 * Obtiene los datos si el resultado es exitoso, null en caso contrario
 */
fun <T> SyncResult<T>.getDataOrNull(): T? = if (this is SyncResult.Success) data else null

/**
 * Obtiene el mensaje de error si hay algún error, null si es exitoso
 */
fun <T> SyncResult<T>.getErrorMessage(): String? = when (this) {
    is SyncResult.BusinessError -> message
    is SyncResult.NetworkError -> message
    is SyncResult.UnknownError -> exception.message ?: "Error desconocido"
    is SyncResult.Success -> null
}

/**
 * Convierte una excepción a SyncResult.UnknownError
 */
fun Exception.toSyncResult(): SyncResult<Nothing> = SyncResult.UnknownError(this)

/**
 * Convierte una respuesta de la API a SyncResult
 */
fun <T> retrofit2.Response<com.nutrizulia.data.remote.dto.ApiResponseDto<T>>.toSyncResult(): SyncResult<T> {
    return if (isSuccessful) {
        val body = body()
        if (body != null) {
            when (body.status) {
                in 200..299 -> SyncResult.Success(body.data!!, body.message)
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
 * Convierte una respuesta de la API a SyncResult con transformación personalizada
 */
inline fun <T, R> retrofit2.Response<com.nutrizulia.data.remote.dto.ApiResponseDto<T>>.toSyncResult(
    transform: (com.nutrizulia.data.remote.dto.ApiResponseDto<T>) -> SyncResult<R>
): SyncResult<R> {
    return if (isSuccessful) {
        val body = body()
        if (body != null) {
            when (body.status) {
                in 200..299 -> transform(body)
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