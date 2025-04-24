package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutrizulia.data.local.entity.ParroquiaEntity

@Dao
interface ParroquiaDao {

    @Query("SELECT * FROM parroquias WHERE cod_entidad_ine = :codEntidad AND cod_municipio_ine = :codMunicipio")
    suspend fun getParroquias(codEntidad: String, codMunicipio: String): List<ParroquiaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParroquias(parroquias: List<ParroquiaEntity>): List<Long>

}