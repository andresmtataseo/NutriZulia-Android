package com.nutrizulia.data.local.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import java.time.LocalDate
import java.time.LocalDateTime

@DatabaseView(
    viewName = "actividades_con_tipos",
    value = """
        SELECT
            a.id AS actividadId,
            a.usuario_institucion_id AS usuarioInstitucionId,
            t.id AS idActividad,
            t.nombre AS nombreActividad,
            a.fecha AS fechaActividad,
            a.direccion AS direccionActividad,
            a.descripcion_general AS descripcionGeneralActividad,
            a.cantidad_participantes AS cantidadParticipantesActividad,
            a.cantidad_sesiones AS cantidadSesionesActividad,
            a.duracion_minutos AS duracionMinutosActividad,
            a.tema_principal AS temaPrincipalActividad,
            a.programas_implementados AS programasImplementadosActividad,
            a.url_evidencia AS urlEvidenciaActividad,
            a.updated_at AS ultimaActualizacionActividad
        FROM actividades AS a
        INNER JOIN tipos_actividades AS t ON a.id = t.id
    """
)
data class ActividadConTipo(
    @ColumnInfo(name = "actividadId")
    val actividadId: String,
    @ColumnInfo(name = "usuarioInstitucionId")
    val usuarioInstitucionId: Int,
    @ColumnInfo(name = "idActividad")
    val idActividad: Int,
    @ColumnInfo(name = "nombreActividad")
    val nombreActividad: String,
    @ColumnInfo(name = "fechaActividad")
    val fechaActividad: LocalDate,
    @ColumnInfo(name = "direccionActividad")
    val direccionActividad: String?,
    @ColumnInfo(name = "descripcionGeneralActividad")
    val descripcionGeneralActividad: String?,
    @ColumnInfo(name = "cantidadParticipantesActividad")
    val cantidadParticipantesActividad: Int?,
    @ColumnInfo(name = "cantidadSesionesActividad")
    val cantidadSesionesActividad: Int?,
    @ColumnInfo(name = "duracionMinutosActividad")
    val duracionMinutosActividad: Int?,
    @ColumnInfo(name = "temaPrincipalActividad")
    val temaPrincipalActividad: String?,
    @ColumnInfo(name = "programasImplementadosActividad")
    val programasImplementadosActividad: String?,
    @ColumnInfo(name = "urlEvidenciaActividad")
    val urlEvidenciaActividad: String?,
    @ColumnInfo(name = "ultimaActualizacionActividad")
    val ultimaActualizacionActividad: LocalDateTime
)