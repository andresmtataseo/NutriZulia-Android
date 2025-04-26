package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutrizulia.data.local.entity.MunicipioEntity

@Dao
interface MunicipioDao {

    @Query("SELECT * FROM municipios WHERE cod_entidad_ine = :codEntidad AND cod_municipio_ine = :codMunicipio")
    suspend fun getMunicipio(codEntidad: String, codMunicipio: String): MunicipioEntity

    @Query("SELECT * FROM municipios WHERE cod_entidad_ine = :codEntidad")
    suspend fun getMunicipios(codEntidad: String): List<MunicipioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMunicipios(municipios: List<MunicipioEntity>): List<Long>

}