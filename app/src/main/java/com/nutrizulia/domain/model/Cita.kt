package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.CitaEntity

data class Cita(
    val id: Int,
    val usuarioId: Int,
    val pacienteId: Int,
    val tipoCita: String,
    val especialidad: String,
    val motivoCita: String?,
    val fechaProgramada: String,
    val horaProgramada: String,
    val estado: String
)

fun CitaEntity.toDomain() = Cita(id, usuarioId, pacienteId, tipoCita, especialidad, motivoCita, fechaProgramada, horaProgramada, estado)
