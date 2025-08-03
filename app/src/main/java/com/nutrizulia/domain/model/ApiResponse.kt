package com.nutrizulia.domain.model

import com.nutrizulia.data.remote.dto.ApiResponseDto


data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T? = null,
    val timestamp: String,
    val path: String,
    val errors: Map<String, String>? = null
)

fun ApiResponseDto<Any>.toDomain() = ApiResponse(
    status = status,
    message = message,
    data = data,
    timestamp = timestamp,
    path = path,
    errors = errors
)