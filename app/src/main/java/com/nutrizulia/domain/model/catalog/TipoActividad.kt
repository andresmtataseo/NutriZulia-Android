package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.TipoActividadEntity

data class TipoActividad(
    val id: Int,
    val nombre: String
)

fun TipoActividadEntity.toDomain() = TipoActividad(
    id = id,
    nombre = nombre
)