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

    @Query("SELECT * FROM detalles_obstetricias WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<DetalleObstetriciaEntity>

    @Insert
    suspend fun insert(detalleObstetricia: DetalleObstetriciaEntity): Long

    @Insert
    suspend fun insertAll(detallesObstetricia: List<DetalleObstetriciaEntity>): List<Long>

    @Upsert
    suspend fun upsert(detalleObstetricia: DetalleObstetriciaEntity)

    @Upsert
    suspend fun upsertAll(detallesObstetricia: List<DetalleObstetriciaEntity>)

    @Delete
    suspend fun delete(detalleObstetricia: DetalleObstetriciaEntity): Int

    @Query("DELETE FROM detalles_obstetricias")
    suspend fun deleteAll(): Int

}