package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.RiesgoBiologicoEntity

data class RiesgoBiologico(
    val id: Int,
    val nombre: String,
    val genero: String,
    val edadMesMinima: Int?,
    val edadMesMaxima: Int?
)

fun RiesgoBiologicoEntity.toDomain() = RiesgoBiologico(
    id = id,
    nombre = nombre,
    genero = genero,
    edadMesMinima = edadMesMinima,
    edadMesMaxima = edadMesMaxima
)