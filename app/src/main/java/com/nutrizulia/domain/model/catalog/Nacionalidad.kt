package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.NacionalidadEntity

data class Nacionalidad(
    val id: Int,
    val nombre: String
)

fun NacionalidadEntity.toDomain() = Nacionalidad(
    id = id,
    nombre = nombre
)