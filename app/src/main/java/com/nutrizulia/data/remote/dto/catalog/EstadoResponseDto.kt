package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.EstadoEntity

data class EstadoResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

fun EstadoResponseDto.toEntity() = EstadoEntity(
    id = id,
    nombre = nombre
)