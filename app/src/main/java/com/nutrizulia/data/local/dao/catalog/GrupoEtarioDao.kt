package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.GrupoEtarioEntity

@Dao
interface GrupoEtarioDao {

    @Query("SELECT * FROM grupos_etarios")
    suspend fun findAll(): List<GrupoEtarioEntity>

    @Insert
    suspend fun insertAll(gruposEtarios: List<GrupoEtarioEntity>): List<Long>

    @Query("DELETE FROM grupos_etarios")
    suspend fun deleteAll(): Int

}