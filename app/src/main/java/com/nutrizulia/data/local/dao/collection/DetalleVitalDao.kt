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

    @Query("SELECT * FROM detalles_vitales WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<DetalleVitalEntity>

    @Query("SELECT * FROM detalles_vitales WHERE is_synced = 0")
    suspend fun findAllNotSynced(): List<DetalleVitalEntity>

    @Insert
    suspend fun insert(detalleVital: DetalleVitalEntity): Long

    @Insert
    suspend fun insertAll(detallesVitales: List<DetalleVitalEntity>): List<Long>

    @Upsert
    suspend fun upsert(detalleVital: DetalleVitalEntity)

    @Upsert
    suspend fun upsertAll(detallesVitales: List<DetalleVitalEntity>)

    @Query("DELETE FROM detalles_vitales")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(detalleVital: DetalleVitalEntity): Int

}