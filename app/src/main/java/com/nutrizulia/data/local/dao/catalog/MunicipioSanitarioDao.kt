package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.MunicipioSanitarioEntity

@Dao
interface MunicipioSanitarioDao {

    @Query("SELECT * FROM municipios_sanitarios WHERE estado_id = :estadoId")
    suspend fun findAllByEstadoId(estadoId: Int): List<MunicipioSanitarioEntity>

    @Insert
    suspend fun insertAll(municipiosSanitarios: List<MunicipioSanitarioEntity>): List<Long>

    @Query("DELETE FROM municipios_sanitarios")
    suspend fun deleteAll(): Int

}