package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.ActividadEntity

data class Actividad(
    val usuarioId: Int,
    val tipoActividad: String,
    val fecha: String
)

fun ActividadEntity.toDomain() = Actividad(usuarioId, tipoActividad, fecha)
