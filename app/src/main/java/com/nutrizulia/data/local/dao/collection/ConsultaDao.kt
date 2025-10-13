package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.ConsultaEntity
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.pojo.DailyAppointmentCount
import java.time.LocalDateTime

@Dao
interface ConsultaDao {

    @Query("""SELECT * FROM consultas WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY fecha_hora_programada ASC""")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<ConsultaEntity>

    @Query("""
        SELECT * FROM consultas 
        WHERE DATE(fecha_hora_programada) <= DATE('now', '-1 day') 
        AND (estado = 'PENDIENTE' OR estado = 'REPROGRAMADA')
        ORDER BY fecha_hora_programada ASC
    """)
    suspend fun findPreviousDayPendingAppointments(): List<ConsultaEntity>

    @Query("""
        SELECT DATE(fecha_hora_programada) as date, COUNT(id) as count
        FROM consultas
        WHERE usuario_institucion_id = :usuarioInstitucionId AND (estado = 'PENDIENTE' OR estado = 'REPROGRAMADA')
        GROUP BY DATE(fecha_hora_programada)
    """)
    suspend fun getAppointmentCountsByDay(usuarioInstitucionId: Int): List<DailyAppointmentCount>

    @Query("SELECT * FROM consultas WHERE paciente_id = :pacienteId AND (estado = 'PENDIENTE' OR estado = 'REPROGRAMADA')")
    suspend fun findConsultaProgramadaByPacienteId(pacienteId: String): ConsultaEntity?

    @Query("SELECT COUNT(id) FROM consultas WHERE paciente_id = :pacienteId")
    suspend fun countConsultaByPacienteId(pacienteId: String): Int
    @Query("SELECT * FROM consultas WHERE id = :id")
    suspend fun findConsultaProgramadaById(id: String): ConsultaEntity?

    @Query("SELECT COUNT(*) FROM consultas WHERE is_synced = 0 AND usuario_institucion_id = :usuarioInstitucionId")
    suspend fun countNotSynced(usuarioInstitucionId: Int): Int
    @Insert
    suspend fun insertAll(consultas: List<ConsultaEntity>): List<Long>

    @Upsert
    suspend fun upsert(consulta: ConsultaEntity): Long

    @Query("UPDATE consultas SET is_synced = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: String, timestamp: LocalDateTime)

    @Query("UPDATE consultas SET estado = :estado WHERE id = :id")
    suspend fun updateEstadoById(id: String, estado: Estado)

    @Query("""
        UPDATE consultas 
        SET estado = 'NO_ASISTIO', updated_at = :timestamp, is_synced = 0 
        WHERE DATE(fecha_hora_programada) <= DATE('now', '-1 day') 
        AND (estado = 'PENDIENTE' OR estado = 'REPROGRAMADA')
    """)
    suspend fun updatePreviousDayPendingAppointmentsToNoShow(timestamp: LocalDateTime): Int

    // Consultas para sincronización
    @Query("SELECT * FROM consultas WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<ConsultaEntity>

    @Query("SELECT * FROM consultas WHERE is_synced = 0 AND usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): List<ConsultaEntity>

    @Query(
        """
        SELECT * FROM consultas 
        WHERE usuario_institucion_id = :usuarioInstitucionId 
        AND (
            (fecha_hora_programada IS NOT NULL AND DATE(fecha_hora_programada) = DATE('now'))
            OR (fecha_hora_real IS NOT NULL AND DATE(fecha_hora_real) = DATE('now'))
        )
        ORDER BY fecha_hora_programada ASC
        """
    )
    suspend fun findConsultasDelMesActual(usuarioInstitucionId: Int): List<ConsultaEntity>

    @Query("""
        SELECT * FROM consultas 
        WHERE usuario_institucion_id = :usuarioInstitucionId 
        ORDER BY updated_at DESC 
        LIMIT 1
    """)
    suspend fun findUltimaConsultaRealizada(usuarioInstitucionId: Int): ConsultaEntity?

    @Upsert
    suspend fun upsertAll(consultas: List<ConsultaEntity>)

    // ===== Nuevas consultas para recordatorios locales =====
    @Query("SELECT COUNT(*) FROM consultas WHERE usuario_institucion_id = :usuarioInstitucionId AND (estado = 'PENDIENTE' OR estado = 'REPROGRAMADA')")
    suspend fun countPendingOrRescheduled(usuarioInstitucionId: Int): Int

    @Query("""
        SELECT * FROM consultas
        WHERE usuario_institucion_id = :usuarioInstitucionId 
        AND (estado = 'PENDIENTE' OR estado = 'REPROGRAMADA')
        AND fecha_hora_programada IS NOT NULL
        AND fecha_hora_programada > :start
        ORDER BY fecha_hora_programada ASC
    """)
    suspend fun findUpcomingPendingOrRescheduled(usuarioInstitucionId: Int, start: LocalDateTime): List<ConsultaEntity>

    @Query(
        """
        SELECT COUNT(*) FROM consultas
        WHERE usuario_institucion_id = :usuarioInstitucionId 
        AND (estado = 'PENDIENTE' OR estado = 'REPROGRAMADA')
        AND fecha_hora_programada IS NOT NULL
        AND fecha_hora_programada >= :start AND fecha_hora_programada <= :end
        """
    )
    suspend fun countPendingOrRescheduledBetween(usuarioInstitucionId: Int, start: LocalDateTime, end: LocalDateTime): Int
}