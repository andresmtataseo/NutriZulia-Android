package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.EtniaEntity

data class EtniaResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

fun EtniaResponseDto.toEntity() = EtniaEntity(
    id = id,
    nombre = nombre
)