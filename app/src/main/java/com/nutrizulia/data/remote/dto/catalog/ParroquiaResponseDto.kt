package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.ParroquiaEntity

data class ParroquiaResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("municipio_id") val municipioId: Int,
    @SerializedName("nombre") val nombre: String
)

fun ParroquiaResponseDto.toEntity() = ParroquiaEntity(
    id = id,
    municipioId = municipioId,
    nombre = nombre
)