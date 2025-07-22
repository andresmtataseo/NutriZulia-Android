package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.MunicipioEntity

@Dao
interface MunicipioDao {

    @Query("SELECT * FROM municipios WHERE estado_id = :estadoId")
    suspend fun findAllByEstadoId(estadoId: Int): List<MunicipioEntity>

    @Query("SELECT * FROM municipios WHERE id = :id")
    suspend fun findMunicipioById(id: Int): MunicipioEntity?

    @Query("SELECT * FROM municipios")
    suspend fun findAll(): List<MunicipioEntity>

    @Insert
    suspend fun insertAll(municipios: List<MunicipioEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(municipios: List<MunicipioEntity>): List<Long>

    @Query("DELETE FROM municipios")
    suspend fun deleteAll(): Int

}