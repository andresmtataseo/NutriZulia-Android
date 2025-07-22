package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.RiesgoBiologicoEntity

@Dao
interface RiesgoBiologicoDao {

    @Query("SELECT * FROM riesgos_biologicos\n" +
            "WHERE (genero = :genero OR genero = 'A')\n" +
            "  AND (:edadMeses BETWEEN edad_mes_minima AND edad_mes_maxima\n" +
            "       OR (edad_mes_minima IS NULL AND :edadMeses <= edad_mes_maxima)\n" +
            "       OR (edad_mes_maxima IS NULL AND :edadMeses >= edad_mes_minima)\n" +
            "       OR (edad_mes_minima IS NULL AND edad_mes_maxima IS NULL))\n" +
            "ORDER BY nombre")
    suspend fun findAllByGeneroAndMeses(genero: String, edadMeses: Int): List<RiesgoBiologicoEntity>

    @Query("SELECT * FROM riesgos_biologicos ORDER BY nombre")
    suspend fun findAll(): List<RiesgoBiologicoEntity>

    @Query("SELECT COUNT(*) FROM riesgos_biologicos")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(riesgosBiologicos: List<RiesgoBiologicoEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(riesgosBiologicos: List<RiesgoBiologicoEntity>): List<Long>

    @Query("DELETE FROM riesgos_biologicos")
    suspend fun deleteAll(): Int

}