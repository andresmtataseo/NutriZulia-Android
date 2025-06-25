package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.MunicipioSanitarioEntity

data class MunicipioSanitario(
    val id: Int,
    val estadoId: Int,
    val nombre: String
)

fun MunicipioSanitarioEntity.toDomain() = MunicipioSanitario(
    id = id,
    estadoId = estadoId,
    nombre = nombre
)