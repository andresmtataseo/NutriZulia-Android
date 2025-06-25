package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.EspecialidadEntity

@Dao
interface EspecialidadDao {

    @Query("SELECT * FROM especialidades")
    suspend fun findAll(): List<EspecialidadEntity>

    @Insert
    suspend fun insertAll(especialidades: List<EspecialidadEntity>): List<Long>

    @Query("DELETE FROM especialidades")
    suspend fun deleteAll(): Int


}