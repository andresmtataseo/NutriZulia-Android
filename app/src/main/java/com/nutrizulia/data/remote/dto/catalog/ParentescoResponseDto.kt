package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.ParentescoEntity

data class ParentescoResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

fun ParentescoResponseDto.toEntity() = ParentescoEntity(
    id = id,
    nombre = nombre
)