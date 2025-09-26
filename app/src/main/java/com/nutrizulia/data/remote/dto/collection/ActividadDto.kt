package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.ActividadEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class ActividadDto (
    @SerializedName("id") val id: String,
    @SerializedName("usuario_institucion_id") val usuarioInstitucionId: Int,
    @SerializedName("tipo_actividad_id") val tipoActividadId: Int,
    @SerializedName("fecha") val fecha: LocalDate,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("descripcion_general") val descripcionGeneral: String?,
    @SerializedName("cantidad_participantes") val cantidadParticipantes: Int?,
    @SerializedName("cantidad_sesiones") val cantidadSesiones: Int?,
    @SerializedName("duracion_minutos") val duracionMinutos: Int?,
    @SerializedName("tema_principal") val temaPrincipal: String?,
    @SerializedName("programas_implementados") val programasImplementados: String?,
    @SerializedName("url_evidencia") val urlEvidencia: String?,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean,
)

fun ActividadDto.toEntity() = ActividadEntity(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    tipoActividadId = tipoActividadId,
    fecha = fecha,
    direccion = direccion,
    descripcionGeneral = descripcionGeneral,
    cantidadParticipantes = cantidadParticipantes,
    cantidadSesiones = cantidadSesiones,
    duracionMinutos = duracionMinutos,
    temaPrincipal = temaPrincipal,
    programasImplementados = programasImplementados,
    urlEvidencia = urlEvidencia,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = true
)