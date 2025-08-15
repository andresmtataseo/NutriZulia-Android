package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.DetalleAntropometricoEntity
import java.time.LocalDateTime

@Dao
interface DetalleAntropometricoDao {

    @Query("SELECT * FROM detalles_antropometricos WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): DetalleAntropometricoEntity?

    @Query("SELECT * FROM detalles_antropometricos WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<DetalleAntropometricoEntity>

    @Query("SELECT * FROM detalles_antropometricos WHERE is_synced = 0")
    suspend fun findAllNotSynced(): List<DetalleAntropometricoEntity>

    @Query("SELECT COUNT(*) FROM detalles_antropometricos WHERE is_synced = 0")
    suspend fun countNotSynced(): Int

    @Insert
    suspend fun insert(detalleAntropometrico: DetalleAntropometricoEntity): Long

    @Insert
    suspend fun insertAll(detallesAntropometricos: List<DetalleAntropometricoEntity>): List<Long>

    @Upsert
    suspend fun upsert(detalleAntropometrico: DetalleAntropometricoEntity)

    @Query("UPDATE detalles_antropometricos SET is_synced = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: String, timestamp: LocalDateTime)

    @Upsert
    suspend fun upsertAll(detallesAntropometricos: List<DetalleAntropometricoEntity>)

    @Delete
    suspend fun delete(detalleAntropometrico: DetalleAntropometricoEntity): Int

    @Query("DELETE FROM detalles_antropometricos")
    suspend fun deleteAll(): Int

}