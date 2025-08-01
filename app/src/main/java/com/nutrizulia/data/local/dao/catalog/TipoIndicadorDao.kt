package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.TipoIndicadorEntity

@Dao
interface TipoIndicadorDao {

    @Query("SELECT * FROM tipos_indicadores")
    suspend fun findAll(): List<TipoIndicadorEntity>

    @Insert
    suspend fun insertAll(tiposIndicadores: List<TipoIndicadorEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(tiposIndicadores: List<TipoIndicadorEntity>): List<Long>

    @Query("DELETE FROM tipos_indicadores")
    suspend fun deleteAll(): Int

}