package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.ConsultaEntity

data class Consulta(
    val id: Int,
    val usuarioId: Int,
    val pacienteId: Int,
    val citaId: Int?,
    val actividadId: Int?,
    val fecha: String,
    val hora: String,
    val diagnosticoPrincipal: String?,
    val diagnosticoSecundario: String?,
    val observaciones: String?
)

fun ConsultaEntity.toDomain() = Consulta(id, usuarioId, pacienteId, citaId, actividadId, fecha, hora, diagnosticoPrincipal, diagnosticoSecundario, observaciones)