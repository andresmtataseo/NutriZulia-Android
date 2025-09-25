package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.DetalleObstetriciaEntity
import java.time.LocalDateTime

@Dao
interface DetalleObstetriciaDao {

    @Query("SELECT * FROM detalles_obstetricias WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): DetalleObstetriciaEntity?

    @Query("""
        SELECT dob.* FROM detalles_obstetricias dob 
        INNER JOIN consultas c ON dob.consulta_id = c.id 
        WHERE c.paciente_id = :pacienteId AND dob.is_deleted = 0 AND c.is_deleted = 0
        ORDER BY dob.updated_at DESC 
        LIMIT 1
    """)
    suspend fun findLatestByPacienteId(pacienteId: String): DetalleObstetriciaEntity?

    @Query("SELECT * FROM detalles_obstetricias WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<DetalleObstetriciaEntity>

    @Query("SELECT dob.* FROM detalles_obstetricias dob INNER JOIN consultas c ON dob.consulta_id = c.id WHERE dob.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): List<DetalleObstetriciaEntity>

    @Query("SELECT COUNT(*) FROM detalles_obstetricias dob INNER JOIN consultas c ON dob.consulta_id = c.id WHERE dob.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun countNotSynced(usuarioInstitucionId: Int): Int

    @Insert
    suspend fun insert(detalleObstetricia: DetalleObstetriciaEntity): Long

    @Insert
    suspend fun insertAll(detallesObstetricia: List<DetalleObstetriciaEntity>): List<Long>

    @Upsert
    suspend fun upsert(detalleObstetricia: DetalleObstetriciaEntity)

    @Upsert
    suspend fun upsertAll(detallesObstetricia: List<DetalleObstetriciaEntity>)

    @Query("UPDATE detalles_obstetricias SET is_synced = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: String, timestamp: LocalDateTime)

    @Delete
    suspend fun delete(detalleObstetricia: DetalleObstetriciaEntity): Int

    @Query("DELETE FROM detalles_obstetricias")
    suspend fun deleteAll(): Int

}