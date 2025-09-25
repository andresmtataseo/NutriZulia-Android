package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.DetalleVitalEntity
import java.time.LocalDateTime

@Dao
interface DetalleVitalDao {

    @Query("SELECT * FROM detalles_vitales WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): DetalleVitalEntity?

    @Query("""
        SELECT dv.* FROM detalles_vitales dv 
        INNER JOIN consultas c ON dv.consulta_id = c.id 
        WHERE c.paciente_id = :pacienteId AND dv.is_deleted = 0 AND c.is_deleted = 0
        ORDER BY dv.updated_at DESC 
        LIMIT 1
    """)
    suspend fun findLatestByPacienteId(pacienteId: String): DetalleVitalEntity?

    @Query("SELECT * FROM detalles_vitales WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<DetalleVitalEntity>

    @Query("SELECT dv.* FROM detalles_vitales dv INNER JOIN consultas c ON dv.consulta_id = c.id WHERE dv.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): List<DetalleVitalEntity>

    @Query("SELECT COUNT(*) FROM detalles_vitales dv INNER JOIN consultas c ON dv.consulta_id = c.id WHERE dv.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun countNotSynced(usuarioInstitucionId: Int): Int

    @Insert
    suspend fun insert(detalleVital: DetalleVitalEntity): Long

    @Insert
    suspend fun insertAll(detallesVitales: List<DetalleVitalEntity>): List<Long>

    @Upsert
    suspend fun upsert(detalleVital: DetalleVitalEntity)

    @Upsert
    suspend fun upsertAll(detallesVitales: List<DetalleVitalEntity>)

    @Query("UPDATE detalles_vitales SET is_synced = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: String, timestamp: LocalDateTime)

    @Query("DELETE FROM detalles_vitales")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(detalleVital: DetalleVitalEntity): Int

}