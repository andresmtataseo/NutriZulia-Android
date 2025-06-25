package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.EstadoEntity

data class Estado(
    val id: Int,
    val nombre: String
)

fun EstadoEntity.toDomain() = Estado(
    id = id,
    nombre = nombre
)