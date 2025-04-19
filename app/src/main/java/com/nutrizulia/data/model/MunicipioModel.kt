package com.nutrizulia.data.model

import com.google.gson.annotations.SerializedName

data class MunicipioModel (
    @SerializedName("cod_municipio_ine") val codMunicipioIne: String,
    @SerializedName("municipio_ine") val municipioIne: String
)

data class MunicipioResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("method") val message: String,
    @SerializedName("data") val data: List<MunicipioModel>
)