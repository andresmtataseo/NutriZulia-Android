package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.ActividadEntity
import java.time.LocalDateTime

@Dao
interface ActividadDao {

    @Query("SELECT * FROM actividades WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY fecha DESC")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<ActividadEntity>

    @Query("SELECT * FROM actividades WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<ActividadEntity>

    @Insert
    suspend fun insertAll(actividades: List<ActividadEntity>): List<Long>

    @Insert
    suspend fun insert(actividad: ActividadEntity): Long

    @Update
    suspend fun update(actividad: ActividadEntity): Int

    @Upsert
    suspend fun upsert(actividad: ActividadEntity)

    @Upsert
    suspend fun upsertAll(actividades: List<ActividadEntity>)

    @Query("DELETE FROM actividades")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(actividad: ActividadEntity): Int
}