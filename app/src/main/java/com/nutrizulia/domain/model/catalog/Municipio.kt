package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.MunicipioEntity

data class Municipio(
    val id: Int,
    val estadoId: Int,
    val nombre: String
)

fun MunicipioEntity.toDomain() = Municipio(
    id = id,
    estadoId = estadoId,
    nombre = nombre
)