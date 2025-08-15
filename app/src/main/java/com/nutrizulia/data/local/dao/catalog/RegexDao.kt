package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.RegexEntity

@Dao
interface RegexDao {

    @Query("SELECT * FROM regex WHERE nombre = :nombre")
    suspend fun findByNombre(nombre: String): RegexEntity?

    @Insert
    suspend fun insertAll(regex: List<RegexEntity>): List<Long>

    @Transaction


    @Upsert
    suspend fun upsertAll(regex: List<RegexEntity>): List<Long>

    @Query("DELETE FROM regex")
    suspend fun deleteAll(): Int

}