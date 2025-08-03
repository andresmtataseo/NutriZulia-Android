package com.nutrizulia.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class CheckAuthResponseDto(
    @SerializedName("cedula") val cedula: String,
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("authenticated") val authenticated: Boolean,
    @SerializedName("tokenValid") val tokenValid: Boolean
)