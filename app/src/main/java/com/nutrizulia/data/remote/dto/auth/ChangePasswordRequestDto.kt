package com.nutrizulia.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequestDto(
    @SerializedName("clave_actual") val claveActual: String,
    @SerializedName("clave_nueva") val claveNueva: String,
    @SerializedName("clave_nueva_confirmacion") val claveNuevaConfirmacion: String
)
