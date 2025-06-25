package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.ParroquiaEntity

@Dao
interface ParroquiaDao {

    @Query("SELECT * FROM parroquias WHERE municipio_id = :municipioId")
    suspend fun findAllByMunicipioId(municipioId: Int): List<ParroquiaEntity>

    @Insert
    suspend fun insertAll(parroquias: List<ParroquiaEntity>): List<Long>

    @Query("DELETE FROM parroquias")
    suspend fun deleteAll(): Int

}