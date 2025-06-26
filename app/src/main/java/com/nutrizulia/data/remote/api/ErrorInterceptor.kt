package com.nutrizulia.data.remote.api

import com.google.gson.Gson
import com.nutrizulia.data.remote.dto.ApiErrorResponseDto
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            val errorMessage = try {
                val apiError = Gson().fromJson(errorBody, ApiErrorResponseDto::class.java)
                apiError.message
            } catch (e: Exception) {
                "Error desconocido"
            }

            throw IOException(errorMessage)
        }

        return response
    }
}
