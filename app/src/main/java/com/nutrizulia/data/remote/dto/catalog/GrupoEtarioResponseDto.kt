package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.GrupoEtarioEntity

data class GrupoEtarioResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("edad_mes_minima") val edadMesMinima: Int,
    @SerializedName("edad_mes_maxima") val edadMesMaxima: Int
)

fun GrupoEtarioResponseDto.toEntity() = GrupoEtarioEntity(
    id = id,
    nombre = nombre,
    edadMesMinima = edadMesMinima,
    edadMesMaxima = edadMesMaxima
)