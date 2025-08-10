package com.nutrizulia.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class SaveEmailRequestDto(
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("correo") val correo: String
)