package com.nutrizulia.domain.model

import com.nutrizulia.data.local.dto.EntidadDto

data class Entidad(
    val codEntidad: String,
    val entidad: String
)

fun EntidadDto.toDomain() = Entidad(
    codEntidad = codEntidad,
    entidad = entidad
)