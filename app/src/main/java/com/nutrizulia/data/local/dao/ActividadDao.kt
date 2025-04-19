package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutrizulia.data.local.entity.ActividadEntity

@Dao
interface ActividadDao {

    @Query("SELECT * FROM actividades")
    suspend fun getAllActividades(): List<ActividadEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertActividad(actividad: ActividadEntity): Long

}