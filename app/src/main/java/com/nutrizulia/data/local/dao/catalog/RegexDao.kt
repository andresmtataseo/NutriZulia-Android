package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.RegexEntity

@Dao
interface RegexDao {

    @Query("SELECT * FROM regex WHERE nombre = :nombre")
    suspend fun findByNombre(nombre: String): RegexEntity?

    @Insert
    suspend fun insertAll(regex: List<RegexEntity>): List<Long>

    @Query("DELETE FROM regex")
    suspend fun deleteAll(): Int

}