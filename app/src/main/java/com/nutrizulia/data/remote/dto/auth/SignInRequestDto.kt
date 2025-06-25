package com.nutrizulia.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName("cedula") val cedula: String,
    @SerializedName("clave") val clave: String,
)