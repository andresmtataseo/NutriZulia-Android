package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.dto.EntidadDto
import com.nutrizulia.data.local.dto.MunicipioDto
import com.nutrizulia.data.local.dto.ParroquiaDto
import com.nutrizulia.data.local.entity.UbicacionEntity

@Dao
interface UbicacionDao {

    @Query("SELECT cod_entidad_ine AS codEntidad, entidad_ine AS entidad FROM ubicaciones GROUP BY cod_entidad_ine, entidad_ine")
    suspend fun getEntidades(): List<EntidadDto>

    @Query("SELECT cod_municipio_ine AS codMunicipio, municipio_ine AS municipio FROM ubicaciones WHERE cod_entidad_ine = :codEntidadIne GROUP BY cod_municipio_ine, municipio_ine")
    suspend fun getMunicipios(codEntidadIne: String): List<MunicipioDto>

    @Query("SELECT id, cod_parroquia_ine AS codParroquia, parroquia_ine AS parroquia FROM ubicaciones WHERE cod_municipio_ine = :codMunicipioIne AND cod_entidad_ine = :codEntidadIne")
    suspend fun getParroquias(codEntidadIne: String, codMunicipioIne: String): List<ParroquiaDto>

    @Insert
    suspend fun insertUbicacion(ubicacion: UbicacionEntity): Long


}