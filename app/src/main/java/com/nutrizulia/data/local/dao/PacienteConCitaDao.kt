package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.view.PacienteConCita

@Dao
interface PacienteConCitaDao {

    @Query("SELECT * FROM pacientes_con_citas WHERE usuarioInstitucionId = :usuarioInstitucionId ORDER BY fechaHoraProgramadaConsulta DESC")
    suspend fun findAll(usuarioInstitucionId: Int): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE usuarioInstitucionId = :usuarioInstitucionId " +
            "AND cedulaPaciente LIKE '%' || :filtro || '%' " +
            "AND nombreCompleto LIKE '%' || :filtro || '%' " +
            "AND fechaNacimientoPaciente LIKE '%' || :filtro || '%' " +
            "AND fechaHoraProgramadaConsulta LIKE '%' || :filtro || '%' " +
            "AND estadoConsulta LIKE '%' || :filtro || '%' " +
            "ORDER BY fechaHoraProgramadaConsulta DESC")
    suspend fun findAllByFiltro(usuarioInstitucionId: Int, filtro: String): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE usuarioInstitucionId = :usuarioInstitucionId AND consultaId = :consultaId")
    suspend fun findById(usuarioInstitucionId: Int, consultaId: String): PacienteConCita?
    @Query("SELECT * FROM pacientes_con_citas WHERE estadoConsulta = :estadoPendiente ORDER BY fechaHoraProgramadaConsulta ASC")
    suspend fun getPacientesConCitasPendientes(estadoPendiente: Estado = Estado.PENDIENTE): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE estadoConsulta = :estadoCancelada ORDER BY fechaHoraProgramadaConsulta DESC")
    suspend fun getPacientesConCitasCanceladas(estadoCancelada: Estado = Estado.CANCELADA): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE estadoConsulta = :estadoCompletada ORDER BY fechaHoraProgramadaConsulta DESC")
    suspend fun getPacientesConCitasCompletadas(estadoCompletada: Estado = Estado.COMPLETADA): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE estadoConsulta = :estado ORDER BY fechaHoraProgramadaConsulta DESC")
    suspend fun getPacientesConCitasByEstado(estado: Estado): List<PacienteConCita>
}