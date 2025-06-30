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
            p.cedula AS cedulaPaciente,
            p.nombres || ' ' || p.apellidos AS nombreCompleto,
            p.fecha_nacimiento AS fechaNacimientoPaciente,
            c.id AS consultaId,
            c.fecha_hora_programada AS fechaHoraProgramadaConsulta,
            c.estado AS estadoConsulta
        FROM pacientes AS p
        INNER JOIN consultas AS c ON p.id = c.paciente_id
        WHERE c.fecha_hora_programada IS NOT NULL
    """
)
data class PacienteConCita(
    @ColumnInfo(name = "cedulaPaciente") val cedulaPaciente: String,
    @ColumnInfo(name = "nombreCompleto") val nombreCompleto: String,
    @ColumnInfo(name = "fechaNacimientoPaciente") val fechaNacimientoPaciente: LocalDate,
    @ColumnInfo(name = "consultaId") val consultaId: Int,
    @ColumnInfo(name = "fechaHoraProgramadaConsulta") val fechaHoraProgramadaConsulta: LocalDateTime,
    @ColumnInfo(name = "estadoConsulta") val estadoConsulta: Estado
)