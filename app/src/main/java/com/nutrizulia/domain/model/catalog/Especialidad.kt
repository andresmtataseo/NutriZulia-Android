package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.EspecialidadEntity

data class Especialidad(
    val id: Int,
    val nombre: String
)

fun EspecialidadEntity.toDomain() = Especialidad(
    id = id,
    nombre = nombre
)