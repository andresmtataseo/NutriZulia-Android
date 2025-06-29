package com.nutrizulia.domain.model.user

import com.nutrizulia.data.local.entity.user.InstitucionEntity

data class Institucion(
    val id: Int,
    val municipioSanitarioId: Int,
    val tipoInstitucionId: Int,
    val nombre: String
)

fun InstitucionEntity.toDomain() = Institucion(
    id = id,
    municipioSanitarioId = municipioSanitarioId,
    tipoInstitucionId = tipoInstitucionId,
    nombre = nombre
)