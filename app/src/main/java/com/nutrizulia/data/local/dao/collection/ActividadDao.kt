package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nutrizulia.data.local.entity.collection.ActividadEntity

@Dao
interface ActividadDao {

    @Query("SELECT * FROM actividades WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY fecha DESC")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<ActividadEntity>

    @Insert
    suspend fun insertAll(actividades: List<ActividadEntity>): List<Long>

    @Insert
    suspend fun insert(actividad: ActividadEntity): Long

    @Update
    suspend fun update(actividad: ActividadEntity): Int

    @Query("DELETE FROM actividades")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(actividad: ActividadEntity): Int
}