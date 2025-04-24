package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.EntidadEntity
import com.nutrizulia.data.model.EntidadModel

data class Entidad(
    val codEntidad: String,
    val entidad: String
)

fun EntidadEntity.toDomain() = Entidad(
    codEntidad = codEntidad,
    entidad = entidad
)

fun EntidadModel.toDomain() = Entidad(
    codEntidad = codEntidadIne,
    entidad = entidadIne
)