package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.ParentescoEntity

@Dao
interface ParentescoDao {

    @Query("SELECT * FROM parentescos")
    suspend fun findAll(): List<ParentescoEntity>

    @Insert
    suspend fun insertAll(parentescos: List<ParentescoEntity>): List<Long>

    @Query("DELETE FROM parentescos")
    suspend fun deleteAll(): Int

}