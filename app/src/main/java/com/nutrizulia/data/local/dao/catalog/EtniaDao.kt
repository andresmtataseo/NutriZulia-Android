package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.EtniaEntity

@Dao
interface EtniaDao {

    @Query("SELECT * FROM etnias")
    suspend fun findAll(): List<EtniaEntity>

    @Query("SELECT * FROM etnias WHERE id = :id")
    suspend fun findEtniaById(id: Int): EtniaEntity?

    @Query("SELECT COUNT(*) FROM etnias")
    suspend fun countAll(): Int

    @Insert
    suspend fun insertAll(etnias: List<EtniaEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(etnias: List<EtniaEntity>): List<Long>

    @Query("DELETE FROM etnias")
    suspend fun deleteAll(): Int

}