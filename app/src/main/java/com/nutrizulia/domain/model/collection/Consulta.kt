package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.ConsultaEntity
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import java.time.LocalDateTime

data class Consulta(
    val id: String,
    var usuarioInstitucionId: Int,
    val pacienteId: String,
    val tipoActividadId: Int,
    val especialidadRemitenteId: Int,
    val tipoConsulta: TipoConsulta?,
    val motivoConsulta: String?,
    val fechaHoraProgramada: LocalDateTime?,
    val observaciones: String?,
    val planes: String?,
    val fechaHoraReal: LocalDateTime?,
    val estado: Estado,
    var updatedAt: LocalDateTime,
    var isDeleted: Boolean,
    var isSynced: Boolean
)

fun ConsultaEntity.toDomain() = Consulta(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    pacienteId = pacienteId,
    tipoActividadId = tipoActividadId,
    especialidadRemitenteId = especialidadRemitenteId,
    tipoConsulta = tipoConsulta,
    motivoConsulta = motivoConsulta,
    fechaHoraProgramada = fechaHoraProgramada,
    observaciones = observaciones,
    planes = planes,
    fechaHoraReal = fechaHoraReal,
    estado = estado,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = isSynced
)