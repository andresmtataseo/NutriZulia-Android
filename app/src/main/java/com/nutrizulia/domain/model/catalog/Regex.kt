package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.RegexEntity

data class Regex(
    val nombre: String,
    val expresion: String
)

fun RegexEntity.toDomain() = Regex(
    nombre = nombre,
    expresion = expresion
)