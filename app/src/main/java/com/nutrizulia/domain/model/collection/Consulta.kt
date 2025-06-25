package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.ConsultaEntity
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import java.time.LocalDate
import java.time.LocalDateTime

data class Consulta(
    val id: String,
    val usuarioInstitucionId: Int,
    val pacienteId: String,
    val tipoActividadId: Int,
    val especialidadRemitente: Int,
    val tipoConsulta: TipoConsulta,
    val motivoConsulta: String?,
    val fechaProgramada: LocalDate,
    val observaciones: String?,
    val planes: String?,
    val fechaHoraReal: LocalDateTime,
    val estado: Estado,
    val updatedAt: LocalDateTime
)

fun ConsultaEntity.toDomain() = Consulta(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    pacienteId = pacienteId,
    tipoActividadId = tipoActividadId,
    especialidadRemitente = especialidadRemitente,
    tipoConsulta = tipoConsulta,
    motivoConsulta = motivoConsulta,
    fechaProgramada = fechaProgramada,
    observaciones = observaciones,
    planes = planes,
    fechaHoraReal = fechaHoraReal,
    estado = estado,
    updatedAt = updatedAt
)