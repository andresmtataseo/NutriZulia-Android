package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.RegexEntity

data class Regex(
    val id: Int,
    val nombre: String,
    val expresion: String
)

fun RegexEntity.toDomain() = Regex(
    id = id,
    nombre = nombre,
    expresion = expresion
)