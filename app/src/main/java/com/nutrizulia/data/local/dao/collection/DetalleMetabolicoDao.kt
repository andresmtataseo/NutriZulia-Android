package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.DetalleMetabolicoEntity
import java.time.LocalDateTime

@Dao
interface DetalleMetabolicoDao {

    @Query("SELECT * FROM detalles_metabolicos WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): DetalleMetabolicoEntity?

    @Query("""
        SELECT dm.* FROM detalles_metabolicos dm 
        INNER JOIN consultas c ON dm.consulta_id = c.id 
        WHERE c.paciente_id = :pacienteId AND dm.is_deleted = 0 AND c.is_deleted = 0
        ORDER BY dm.updated_at DESC 
        LIMIT 1
    """)
    suspend fun findLatestByPacienteId(pacienteId: String): DetalleMetabolicoEntity?

    @Query("SELECT * FROM detalles_metabolicos WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<DetalleMetabolicoEntity>

    @Query("SELECT dm.* FROM detalles_metabolicos dm INNER JOIN consultas c ON dm.consulta_id = c.id WHERE dm.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): List<DetalleMetabolicoEntity>

    @Query("SELECT COUNT(*) FROM detalles_metabolicos dm INNER JOIN consultas c ON dm.consulta_id = c.id WHERE dm.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun countNotSynced(usuarioInstitucionId: Int): Int

    @Insert
    suspend fun insert(detalleMetabolico: DetalleMetabolicoEntity): Long

    @Query("UPDATE detalles_metabolicos SET is_synced = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: String, timestamp: LocalDateTime)

    @Insert
    suspend fun insertAll(detallesMetabolicos: List<DetalleMetabolicoEntity>): List<Long>

    @Upsert
    suspend fun upsert(detalleMetabolico: DetalleMetabolicoEntity)

    @Upsert
    suspend fun upsertAll(detallesMetabolicos: List<DetalleMetabolicoEntity>)

    @Delete
    suspend fun delete(detalleMetabolico: DetalleMetabolicoEntity): Int

    @Query("DELETE FROM detalles_metabolicos")
    suspend fun deleteAll(): Int

}