package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.TipoInstitucionEntity

@Dao
interface TipoInstitucionDao {

    @Query("SELECT * FROM tipos_instituciones")
    suspend fun findAll(): List<TipoInstitucionEntity>

    @Insert
    suspend fun insertAll(tiposInstituciones: List<TipoInstitucionEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(tiposInstituciones: List<TipoInstitucionEntity>): List<Long>

    @Query("DELETE FROM tipos_instituciones")
    suspend fun deleteAll(): Int

}