package com.nutrizulia.data.model

import com.google.gson.annotations.SerializedName
import com.nutrizulia.domain.model.Entidad

data class EntidadModel(
    @SerializedName("cod_entidad_ine") val codEntidadIne: String,
    @SerializedName("entidad_ine") val entidadIne: String
)

data class EntidadResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("method") val message: String,
    @SerializedName("data") val data: List<EntidadModel>
)