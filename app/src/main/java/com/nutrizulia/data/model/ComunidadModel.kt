package com.nutrizulia.data.model

import com.google.gson.annotations.SerializedName

data class ComunidadModel(
    @SerializedName("id_comunidad_ine") val idComunidadIne: String,
    @SerializedName("nombre_comunidad") val nombreComunidadIne: String
)

data class ComunidadResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("method") val method: String,
    @SerializedName("data") val data: List<ComunidadModel>
)
