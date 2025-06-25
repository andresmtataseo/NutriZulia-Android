package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.NacionalidadEntity

@Dao
interface NacionalidadDao {

    @Query("SELECT * FROM nacionalidades")
    suspend fun findAll(): List<NacionalidadEntity>

    @Insert
    suspend fun insertAll(nacionalidades: List<NacionalidadEntity>): List<Long>

    @Query("DELETE FROM nacionalidades")
    suspend fun deleteAll(): Int

}