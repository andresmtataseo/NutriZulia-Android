package com.nutrizulia.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class SavePhoneRequestDto(
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("telefono") val telefono: String
)