package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.RegexEntity

data class RegexResponseDto (
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("expresion") val expresion: String
)

fun RegexResponseDto.toEntity() = RegexEntity(
    id = id,
    nombre = nombre,
    expresion = expresion
)