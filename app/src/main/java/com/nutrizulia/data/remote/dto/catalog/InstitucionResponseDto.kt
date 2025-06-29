package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.user.InstitucionEntity

data class InstitucionResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("municipio_sanitario_id") val municipioSanitarioId: Int,
    @SerializedName("tipo_institucion_id") val tipoInstitucionId: Int,
    @SerializedName("nombre") val nombre: String
)

fun InstitucionResponseDto.toEntity() = InstitucionEntity(
    id = id,
    municipioSanitarioId = municipioSanitarioId,
    tipoInstitucionId = tipoInstitucionId,
    nombre = nombre
)