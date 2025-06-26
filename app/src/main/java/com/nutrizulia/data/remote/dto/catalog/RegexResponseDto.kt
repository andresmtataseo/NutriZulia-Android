package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.RegexEntity

data class RegexResponseDto (
    @SerializedName("nombre") val nombre: String,
    @SerializedName("expression") val expresion: String
)

fun RegexResponseDto.toEntity() = RegexEntity(
    nombre = nombre,
    expresion = expresion
)