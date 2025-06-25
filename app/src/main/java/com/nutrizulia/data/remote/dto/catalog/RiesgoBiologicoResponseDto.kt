package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.RiesgoBiologicoEntity

data class RiesgoBiologicoResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("genero") val genero: String,
    @SerializedName("edad_mes_minima") val edadMesMinima: Int?,
    @SerializedName("edad_mes_maxima") val edadMesMaxima: Int?
)

fun RiesgoBiologicoResponseDto.toEntity() = RiesgoBiologicoEntity(
    id = id,
    nombre = nombre,
    genero = genero,
    edadMesMinima = edadMesMinima,
    edadMesMaxima = edadMesMaxima
)