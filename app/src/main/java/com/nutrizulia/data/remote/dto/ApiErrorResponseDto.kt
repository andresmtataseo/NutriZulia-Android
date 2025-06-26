package com.nutrizulia.data.remote.dto

data class ApiErrorResponseDto(
    val status: Int,
    val message: String,
    val timestamp: String,
    val path: String,
    val errors: List<String>? = null
)
