package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.TipoActividadEntity

data class TipoActividadResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

fun TipoActividadResponseDto.toEntity() = TipoActividadEntity(
    id = id,
    nombre = nombre
)