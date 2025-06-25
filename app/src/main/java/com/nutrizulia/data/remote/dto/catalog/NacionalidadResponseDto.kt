package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.NacionalidadEntity

data class NacionalidadResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

fun NacionalidadResponseDto.toEntity() = NacionalidadEntity(
    id = id,
    nombre = nombre
)