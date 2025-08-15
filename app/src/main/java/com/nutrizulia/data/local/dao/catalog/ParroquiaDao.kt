package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.ParroquiaEntity

@Dao
interface ParroquiaDao {

    @Query("SELECT * FROM parroquias WHERE municipio_id = :municipioId")
    suspend fun findAllByMunicipioId(municipioId: Int): List<ParroquiaEntity>

    @Query("SELECT * FROM parroquias WHERE id = :id")
    suspend fun findParroquiaById(id: Int): ParroquiaEntity?

    @Insert
    suspend fun insertAll(parroquias: List<ParroquiaEntity>): List<Long>

    @Transaction


    @Upsert
    suspend fun upsertAll(parroquias: List<ParroquiaEntity>): List<Long>

    @Query("DELETE FROM parroquias")
    suspend fun deleteAll(): Int

}