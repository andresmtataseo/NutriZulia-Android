package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.TipoInstitucionEntity

data class TipoInstitucionResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

fun TipoInstitucionResponseDto.toEntity() = TipoInstitucionEntity(
    id = id,
    nombre = nombre
)