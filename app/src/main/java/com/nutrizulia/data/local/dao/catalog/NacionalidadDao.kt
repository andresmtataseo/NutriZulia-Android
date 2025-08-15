package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.NacionalidadEntity

@Dao
interface NacionalidadDao {

    @Query("SELECT * FROM nacionalidades")
    suspend fun findAll(): List<NacionalidadEntity>

    @Query("SELECT * FROM nacionalidades WHERE id = :id")
    suspend fun findNacionalidadById(id: Int): NacionalidadEntity?

    @Insert
    suspend fun insertAll(nacionalidades: List<NacionalidadEntity>): List<Long>

    @Transaction


    @Upsert
    suspend fun upsertAll(nacionalidades: List<NacionalidadEntity>): List<Long>

    @Query("DELETE FROM nacionalidades")
    suspend fun deleteAll(): Int

}