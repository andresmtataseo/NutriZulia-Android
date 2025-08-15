package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.DetallePediatricoEntity
import java.time.LocalDateTime

@Dao
interface DetallePediatricoDao {

    @Query("SELECT * FROM detalles_pediatricos WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): DetallePediatricoEntity?

    @Query("SELECT * FROM detalles_pediatricos WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<DetallePediatricoEntity>

    @Query("SELECT dp.* FROM detalles_pediatricos dp INNER JOIN consultas c ON dp.consulta_id = c.id WHERE dp.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): List<DetallePediatricoEntity>

    @Query("SELECT COUNT(*) FROM detalles_pediatricos dp INNER JOIN consultas c ON dp.consulta_id = c.id WHERE dp.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun countNotSynced(usuarioInstitucionId: Int): Int

    @Insert
    suspend fun insert(detallePediatrico: DetallePediatricoEntity): Long

    @Insert
    suspend fun insertAll(detallesPediatricos: List<DetallePediatricoEntity>): List<Long>

    @Upsert
    suspend fun upsert(detallePediatrico: DetallePediatricoEntity)

    @Upsert
    suspend fun upsertAll(detallesPediatricos: List<DetallePediatricoEntity>)

    @Query("UPDATE detalles_pediatricos SET is_synced = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: String, timestamp: LocalDateTime)

    @Query("DELETE FROM detalles_pediatricos")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(detallePediatrico: DetallePediatricoEntity): Int

}