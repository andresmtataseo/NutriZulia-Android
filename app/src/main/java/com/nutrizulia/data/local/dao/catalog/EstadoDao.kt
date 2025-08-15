package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.EstadoEntity

@Dao
interface EstadoDao {

    @Query("SELECT * FROM estados")
    suspend fun findAll(): List<EstadoEntity>

    @Query("SELECT * FROM estados WHERE id = :id")
    suspend fun findEstadoById(id: Int): EstadoEntity?

    @Insert
    suspend fun insertAll(estados: List<EstadoEntity>): List<Long>

    @Transaction
    @Upsert
    suspend fun upsertAll(estados: List<EstadoEntity>): List<Long>

    @Query("DELETE FROM estados")
    suspend fun deleteAll(): Int

}