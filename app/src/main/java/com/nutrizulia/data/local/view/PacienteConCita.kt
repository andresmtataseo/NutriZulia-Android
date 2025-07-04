package com.nutrizulia.data.local.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.nutrizulia.data.local.enum.Estado
import java.time.LocalDate
import java.time.LocalDateTime

@DatabaseView(
    viewName = "pacientes_con_citas",
    value = """
        SELECT
            p.id AS pacienteId,
            p.cedula AS cedulaPaciente,
            p.nombres || ' ' || p.apellidos AS nombreCompleto,
            p.fecha_nacimiento AS fechaNacimientoPaciente,
            c.usuario_institucion_id AS usuarioInstitucionId,
            c.id AS consultaId,
            c.fecha_hora_programada AS fechaHoraProgramadaConsulta,
            c.estado AS estadoConsulta
        FROM pacientes AS p
        INNER JOIN consultas AS c ON p.id = c.paciente_id
        WHERE c.fecha_hora_programada IS NOT NULL
    """
)
data class PacienteConCita(
    @ColumnInfo(name = "pacienteId") val pacienteId: String,
    @ColumnInfo(name = "cedulaPaciente") val cedulaPaciente: String,
    @ColumnInfo(name = "nombreCompleto") val nombreCompleto: String,
    @ColumnInfo(name = "fechaNacimientoPaciente") val fechaNacimientoPaciente: LocalDate,
    @ColumnInfo(name = "usuarioInstitucionId") val usuarioInstitucionId: Int,
    @ColumnInfo(name = "consultaId") val consultaId: String,
    @ColumnInfo(name = "fechaHoraProgramadaConsulta") val fechaHoraProgramadaConsulta: LocalDateTime,
    @ColumnInfo(name = "estadoConsulta") val estadoConsulta: Estado
)