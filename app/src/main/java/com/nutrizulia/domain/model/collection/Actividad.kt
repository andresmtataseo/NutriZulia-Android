package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.ActividadEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class Actividad(
    val id: String,
    val usuarioInstitucionId: Int,
    val tipoActividadId: Int,
    val fecha: LocalDate,
    val direccion: String?,
    val descripcionGeneral: String?,
    val cantidadParticipantes: Int?,
    val cantidadSesiones: Int?,
    val duracionMinutos: Int?,
    val temaPrincipal: String?,
    val programasImplementados: String?,
    val urlEvidencia: String?,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean
)

fun ActividadEntity.toDomain() = Actividad(
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
    isDeleted = isDeleted
)