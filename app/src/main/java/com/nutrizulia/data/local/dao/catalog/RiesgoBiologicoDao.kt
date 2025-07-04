package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.RiesgoBiologicoEntity

@Dao
interface RiesgoBiologicoDao {

    @Query("SELECT * FROM riesgos_biologicos WHERE genero = :genero")
    suspend fun findAllByGenero(genero: String): List<RiesgoBiologicoEntity>

    @Insert
    suspend fun insertAll(riesgosBiologicos: List<RiesgoBiologicoEntity>): List<Long>

    @Query("DELETE FROM riesgos_biologicos")
    suspend fun deleteAll(): Int

}