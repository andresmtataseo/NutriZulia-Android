package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.TipoInstitucionEntity

data class TipoInstitucion(
    val id: Int,
    val nombre: String
)

fun TipoInstitucionEntity.toDomain() = TipoInstitucion(
    id = id,
    nombre = nombre
)