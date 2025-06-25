package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.GrupoEtarioEntity

data class GrupoEtario(
    val id: Int,
    val nombre: String,
    val edadMesMinima: Int,
    val edadMesMaxima: Int
)

fun GrupoEtarioEntity.toDomain() = GrupoEtario(
    id = id,
    nombre = nombre,
    edadMesMinima = edadMesMinima,
    edadMesMaxima = edadMesMaxima
)