package com.nutrizulia.data.remote.dto

data class ApiErrorResponseDto(
    val status: Int,
    val message: String,
    val timestamp: String,
    val path: String,
    val errors: Map<String, String>? = null,
    val data: Map<String, Any>? = null
)
