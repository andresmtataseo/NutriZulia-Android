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

    @Query("SELECT * FROM consultas WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY fecha_hora_programada ASC")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<ConsultaEntity>

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

    @Insert
    suspend fun insertAll(consultas: List<ConsultaEntity>): List<Long>

    @Upsert
    suspend fun upsert(consulta: ConsultaEntity): Long

    @Query("UPDATE consultas SET estado = :estado WHERE id = :id")
    suspend fun updateEstadoById(id: String, estado: Estado)

    // Consultas para sincronización
    @Query("SELECT * FROM consultas WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<ConsultaEntity>

    @Upsert
    suspend fun upsertAll(consultas: List<ConsultaEntity>)

}