package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.view.PacienteConCita

@Dao
interface PacienteConCitaDao {

    @Query("SELECT * FROM pacientes_con_citas")
    suspend fun getAllPacientesConCitas(): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE estadoConsulta = :estadoPendiente ORDER BY fechaProgramadaConsulta ASC")
    suspend fun getPacientesConCitasPendientes(estadoPendiente: Estado = Estado.PENDIENTE): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE estadoConsulta = :estadoCancelada ORDER BY fechaProgramadaConsulta DESC")
    suspend fun getPacientesConCitasCanceladas(estadoCancelada: Estado = Estado.CANCELADA): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE estadoConsulta = :estadoCompletada ORDER BY fechaProgramadaConsulta DESC")
    suspend fun getPacientesConCitasCompletadas(estadoCompletada: Estado = Estado.COMPLETADA): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE estadoConsulta = :estado ORDER BY fechaProgramadaConsulta DESC")
    suspend fun getPacientesConCitasByEstado(estado: Estado): List<PacienteConCita>
}