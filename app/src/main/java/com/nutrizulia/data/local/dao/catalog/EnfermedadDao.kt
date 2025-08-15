package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.EnfermedadEntity

@Dao
interface EnfermedadDao {

    @Query("SELECT * FROM enfermedades WHERE genero = :genero AND nombre LIKE '%' || :nombre || '%'")
    suspend fun findAllByGeneroAndNombreLike(genero: String, nombre: String): List<EnfermedadEntity>

    @Query("SELECT COUNT(*) FROM enfermedades")
    suspend fun countAll(): Int

    @Insert
    suspend fun insertAll(enfermedades: List<EnfermedadEntity>): List<Long>

    @Transaction


    @Upsert
    suspend fun upsertAll(enfermedades: List<EnfermedadEntity>): List<Long>

    @Query("DELETE FROM enfermedades")
    suspend fun deleteAll(): Int
}