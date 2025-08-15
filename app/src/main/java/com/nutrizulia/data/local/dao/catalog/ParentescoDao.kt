package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.ParentescoEntity

@Dao
interface ParentescoDao {

    @Query("SELECT * FROM parentescos")
    suspend fun findAll(): List<ParentescoEntity>

    @Query("SELECT * FROM parentescos WHERE id = :id")
    suspend fun findById(id: Int): ParentescoEntity?

    @Insert
    suspend fun insertAll(parentescos: List<ParentescoEntity>): List<Long>

    @Transaction


    @Upsert
    suspend fun upsertAll(parentescos: List<ParentescoEntity>): List<Long>

    @Query("DELETE FROM parentescos")
    suspend fun deleteAll(): Int

}