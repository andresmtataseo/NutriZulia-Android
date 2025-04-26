package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutrizulia.data.local.entity.EntidadEntity

@Dao
interface EntidadDao {

    @Query("SELECT * FROM entidades WHERE cod_entidad_ine = :codEntidad")
    suspend fun getEntidad(codEntidad: String): EntidadEntity

    @Query("SELECT * FROM entidades")
    suspend fun getEntidades(): List<EntidadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntidades(entidades: List<EntidadEntity>): List<Long>

}