package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutrizulia.data.local.entity.ComunidadEntity

@Dao
interface ComunidadDao {

    @Query("SELECT * FROM comunidades WHERE cod_entidad_ine = :codEntidad AND cod_municipio_ine = :codMunicipio AND cod_parroquia_ine = :codParroquia AND id_comunidad_ine = :idComunidad")
    suspend fun getComunidad(codEntidad: String, codMunicipio: String, codParroquia: String, idComunidad: String): ComunidadEntity

    @Query("SELECT * FROM comunidades WHERE cod_entidad_ine = :codEntidad AND cod_municipio_ine = :codMunicipio AND cod_parroquia_ine = :codParroquia")
    suspend fun getComunidades(codEntidad: String, codMunicipio: String, codParroquia: String): List<ComunidadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComunidades(comunidades: List<ComunidadEntity>): List<Long>

}