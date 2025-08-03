package com.nutrizulia.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponseDto<T>(
    @SerializedName("status")
    val status: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("path")
    val path: String,

    @SerializedName("errors")
    val errors: Map<String, String>? = null
)
