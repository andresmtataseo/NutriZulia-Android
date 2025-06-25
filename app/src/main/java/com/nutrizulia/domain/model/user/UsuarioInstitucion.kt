package com.nutrizulia.domain.model.user

import com.nutrizulia.data.local.entity.user.UsuarioInstitucionEntity
import java.time.LocalDate

data class UsuarioInstitucion(
    val id: Int,
    val institucionId: Int,
    val usuarioId: Int,
    val rolId: Int,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate?,
    val isEnabled: Boolean
)

fun UsuarioInstitucionEntity.toDomain() = UsuarioInstitucion(
    id = id,
    institucionId = institucionId,
    usuarioId = usuarioId,
    rolId = rolId,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    isEnabled = isEnabled
)