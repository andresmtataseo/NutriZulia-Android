package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.EspecialidadEntity

data class EspecialidadResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

fun EspecialidadResponseDto.toEntity() = EspecialidadEntity(
    id = id,
    nombre = nombre
)