package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.view.PacienteConCita

@Dao
interface PacienteConCitaDao {

    @Query("SELECT * FROM pacientes_con_citas WHERE usuarioInstitucionId = :usuarioInstitucionId ORDER BY ultimaActualizacion DESC")
    suspend fun findAll(usuarioInstitucionId: Int): List<PacienteConCita>

    @Query("SELECT * FROM pacientes_con_citas WHERE usuarioInstitucionId = :usuarioInstitucionId " +
            "AND (cedulaPaciente LIKE '%' || :filtro || '%' " +
            "OR nombreCompleto LIKE '%' || :filtro || '%' " +
            "OR fechaNacimientoPaciente LIKE '%' || :filtro || '%' " +
            "OR fechaHoraProgramadaConsulta LIKE '%' || :filtro || '%' " +
            "OR estadoConsulta LIKE '%' || :filtro || '%') " +
            "ORDER BY ultimaActualizacion DESC")
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



    @Query("SELECT * FROM pacientes_con_citas WHERE usuarioInstitucionId = :usuarioInstitucionId " +
            "AND (:filtrarEstados = 0 OR estadoConsulta IN (:estados)) " +
            "AND (:filtrarTipos = 0 OR tipoConsulta IN (:tiposConsulta)) " +
            "AND (:fechaInicio IS NULL OR " +
            "    (fechaHoraProgramadaConsulta IS NOT NULL AND DATE(fechaHoraProgramadaConsulta) >= :fechaInicio) OR " +
            "    (fechaHoraRealConsulta IS NOT NULL AND DATE(fechaHoraRealConsulta) >= :fechaInicio)) " +
            "AND (:fechaFin IS NULL OR " +
            "    (fechaHoraProgramadaConsulta IS NOT NULL AND DATE(fechaHoraProgramadaConsulta) <= :fechaFin) OR " +
            "    (fechaHoraRealConsulta IS NOT NULL AND DATE(fechaHoraRealConsulta) <= :fechaFin)) " +
            "ORDER BY ultimaActualizacion DESC")
    suspend fun findAllByCompleteFilters(
        usuarioInstitucionId: Int,
        estados: List<String>,
        tiposConsulta: List<String>,
        fechaInicio: String?,
        fechaFin: String?,
        filtrarEstados: Int,
        filtrarTipos: Int
    ): List<PacienteConCita>
}