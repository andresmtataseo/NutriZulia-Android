package com.nutrizulia.data.remote.dto.auth

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.remote.dto.user.UsuarioResponseDto

data class SignInResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("type") val type: String,
    @SerializedName("user") val user: UsuarioResponseDto,
)