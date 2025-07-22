package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.EspecialidadEntity

@Dao
interface EspecialidadDao {

    @Query("SELECT * FROM especialidades")
    suspend fun findAll(): List<EspecialidadEntity>

    @Query("SELECT * FROM especialidades WHERE id = :id")
    suspend fun findById(id: Int): EspecialidadEntity?

    @Insert
    suspend fun insertAll(especialidades: List<EspecialidadEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(especialidades: List<EspecialidadEntity>): List<Long>

    @Query("DELETE FROM especialidades")
    suspend fun deleteAll(): Int


}