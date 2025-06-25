package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.MunicipioSanitarioEntity

data class MunicipioSanitarioResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("estado_id") val estadoId: Int,
    @SerializedName("nombre") val nombre: String
)

fun MunicipioSanitarioResponseDto.toEntity() = MunicipioSanitarioEntity(
    id = id,
    estadoId = estadoId,
    nombre = nombre
)