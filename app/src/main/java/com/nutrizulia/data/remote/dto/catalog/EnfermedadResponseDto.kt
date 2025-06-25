package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.EnfermedadEntity

data class EnfermedadResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("codigo_internacional") val codigoInternacional: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("genero") val genero: String
)

fun EnfermedadResponseDto.toEntity() = EnfermedadEntity(
        id = id,
        codigoInternacional = codigoInternacional,
        nombre = nombre,
        genero = genero
)