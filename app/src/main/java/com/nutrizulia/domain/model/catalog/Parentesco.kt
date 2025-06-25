package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.ParentescoEntity

data class Parentesco(
    val id: Int,
    val nombre: String
)

fun ParentescoEntity.toDomain() = Parentesco(
    id = id,
    nombre = nombre
)