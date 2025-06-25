package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.MunicipioEntity

@Dao
interface MunicipioDao {

    @Query("SELECT * FROM municipios WHERE estado_id = :estadoId")
    suspend fun findAllByEstadoId(estadoId: Int): List<MunicipioEntity>

    @Insert
    suspend fun insertAll(municipios: List<MunicipioEntity>): List<Long>

    @Query("DELETE FROM municipios")
    suspend fun deleteAll(): Int

}