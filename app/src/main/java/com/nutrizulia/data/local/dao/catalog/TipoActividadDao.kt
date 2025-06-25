package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.TipoActividadEntity

@Dao
interface TipoActividadDao {

    @Query("SELECT * FROM tipos_actividades")
    suspend fun findAll(): List<TipoActividadEntity>

    @Insert
    suspend fun insertAll(tiposActividades: List<TipoActividadEntity>): List<Long>

    @Query("DELETE FROM tipos_actividades")
    suspend fun deleteAll(): Int

}