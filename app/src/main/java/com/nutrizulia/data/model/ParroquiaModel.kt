package com.nutrizulia.data.model

import com.google.gson.annotations.SerializedName

data class ParroquiaModel(
    @SerializedName("cod_parroquia_ine") val codParroquiaIne: String,
    @SerializedName("parroquia_ine") val parroquiaIne: String
)

data class ParroquiaResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("method") val method: String,
    @SerializedName("data") val data: List<ParroquiaModel>
)
