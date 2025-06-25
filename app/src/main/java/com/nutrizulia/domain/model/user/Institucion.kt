package com.nutrizulia.domain.model.user

import com.nutrizulia.data.local.entity.user.InstitucionEntity

data class Institucion(
    val id: Int,
    val municipioSaniitarioId: Int,
    val tipoInstitucionId: Int,
    val nombre: String
)

fun InstitucionEntity.toDomain() = Institucion(
    id = id,
    municipioSaniitarioId = municipioSaniitarioId,
    tipoInstitucionId = tipoInstitucionId,
    nombre = nombre
)