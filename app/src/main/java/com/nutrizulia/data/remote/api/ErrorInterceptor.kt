package com.nutrizulia.data.remote.api

import android.util.Log
import com.google.gson.Gson
import com.nutrizulia.data.remote.dto.ApiErrorResponseDto
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorInterceptor : Interceptor {
    
    companion object {
        private const val TAG = "ErrorInterceptor"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            Log.d(TAG, "Error response - Status: ${response.code}, Body: $errorBody")
            
            val errorMessage = try {
                if (!errorBody.isNullOrBlank()) {
                    val apiError = Gson().fromJson(errorBody, ApiErrorResponseDto::class.java)
                    
                    // Si hay errores de validación específicos, usar el primer error
                    if (!apiError.errors.isNullOrEmpty()) {
                        apiError.errors.values.first()
                    } else if (!apiError.data.isNullOrEmpty()) {
                        // Si hay errores en el campo data, usar el primer error
                        val dataErrors = apiError.data.values.firstOrNull()
                        if (dataErrors is String) {
                            dataErrors
                        } else {
                            apiError.message ?: getDefaultErrorMessage(response.code)
                        }
                    } else {
                        apiError.message ?: getDefaultErrorMessage(response.code)
                    }
                } else {
                    getDefaultErrorMessage(response.code)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing error response", e)
                getDefaultErrorMessage(response.code)
            }

            Log.e(TAG, "HTTP Error ${response.code}: $errorMessage")
            // Lanzar excepción con código de estado para manejo diferenciado (e.g., 403)
            throw ApiHttpException(response.code, errorMessage)
        }

        return response
    }
    
    private fun getDefaultErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            400 -> "Solicitud inválida. Verifique los datos enviados."
            401 -> "No autorizado. Token de autenticación ausente, inválido o expirado."
            403 -> "Acceso prohibido. No tiene permisos para realizar esta acción."
            404 -> "Recurso no encontrado."
            409 -> "Conflicto. Los datos ya existen o están en uso."
            422 -> "Datos no válidos. Verifique la información enviada."
            423 -> "Cuenta bloqueada temporalmente. Recupere su cuenta."
            500 -> "Error interno del servidor. Intente nuevamente más tarde."
            502 -> "Error de conexión con el servidor."
            503 -> "Servicio no disponible temporalmente."
            else -> "Error HTTP $statusCode. Intente nuevamente."
        }
    }
}
