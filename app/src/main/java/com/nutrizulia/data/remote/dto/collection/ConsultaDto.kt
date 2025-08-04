package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.ConsultaEntity
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import java.time.LocalDateTime

data class ConsultaDto(
    @SerializedName("id") val id: String,
    @SerializedName("usuario_institucion_id") val usuarioInstitucionId: Int,
    @SerializedName("paciente_id") val pacienteId: String,
    @SerializedName("tipo_actividad_id") val tipoActividadId: Int,
    @SerializedName("especialidad_remitente_id") val especialidadRemitenteId: Int,
    @SerializedName("tipo_consulta") val tipoConsulta: TipoConsulta,
    @SerializedName("motivo_consulta") val motivoConsulta: String?,
    @SerializedName("fecha_hora_programada") val fechaHoraProgramada: LocalDateTime?,
    @SerializedName("observaciones") val observaciones: String?,
    @SerializedName("planes") val planes: String?,
    @SerializedName("fecha_hora_real") val fechaHoraReal: LocalDateTime?,
    @SerializedName("estado") val estado: Estado,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean
)

fun ConsultaDto.toDomain() = ConsultaEntity(
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
    isSynced = false
)