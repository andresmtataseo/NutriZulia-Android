package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.TipoIndicadorEntity

data class TipoIndicadorResponseDto (
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

fun TipoIndicadorResponseDto.toEntity() = TipoIndicadorEntity(
    id = id,
    nombre = nombre
)