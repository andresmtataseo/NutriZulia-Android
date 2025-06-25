package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.EtniaEntity

data class Etnia(
    val id: Int,
    val nombre: String
)

fun EtniaEntity.toDomain() = Etnia(
    id = id,
    nombre = nombre
)