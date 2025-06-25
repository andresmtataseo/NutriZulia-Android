package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.MunicipioEntity

data class MunicipioResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("estado_id") val estadoId: Int,
    @SerializedName("nombre") val nombre: String
)

fun MunicipioResponseDto.toEntity() = MunicipioEntity(
    id = id,
    estadoId = estadoId,
    nombre = nombre
)