package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.TipoIndicadorEntity

data class TipoIndicador(
    val id: Int,
    val nombre: String
)

fun TipoIndicadorEntity.toDomain() = TipoIndicador(
    id = id,
    nombre = nombre
)