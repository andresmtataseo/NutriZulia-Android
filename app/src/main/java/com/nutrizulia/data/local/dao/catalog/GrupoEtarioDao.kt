package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.GrupoEtarioEntity

@Dao
interface GrupoEtarioDao {

    @Query("SELECT * FROM grupos_etarios")
    suspend fun findAll(): List<GrupoEtarioEntity>

    @Query("""
        SELECT * FROM grupos_etarios 
        WHERE (edad_mes_minima IS NULL OR :edadMes >= edad_mes_minima) 
          AND (edad_mes_maxima IS NULL OR :edadMes <= edad_mes_maxima) 
        LIMIT 1
    """)
    suspend fun findByEdad(edadMes: Int): GrupoEtarioEntity?

    @Insert
    suspend fun insertAll(gruposEtarios: List<GrupoEtarioEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(gruposEtarios: List<GrupoEtarioEntity>): List<Long>

    @Query("DELETE FROM grupos_etarios")
    suspend fun deleteAll(): Int

}