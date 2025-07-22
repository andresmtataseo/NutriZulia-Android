package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoNinoEdadEntity

@Dao
interface ParametroCrecimientoNinoEdadDao {

    @Query("SELECT * FROM parametros_crecimientos_ninos_edad WHERE tipo_indicador_id = :tipoIndicadorId AND grupo_etario_id = :grupoEtarioId AND genero = :genero AND edad_mes = :edadMes")
    suspend fun findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndEdadMes(
        tipoIndicadorId: Int,
        grupoEtarioId: Int,
        genero: String,
        edadMes: Int
    ): ParametroCrecimientoNinoEdadEntity?

    @Query("SELECT * FROM parametros_crecimientos_ninos_edad WHERE grupo_etario_id = :grupoEtarioId AND genero = :genero AND edad_mes = :edadMes")
    suspend fun findAllByGrupoEtarioIdAndGeneroAndEdadMes(
        grupoEtarioId: Int,
        genero: String,
        edadMes: Int
    ): List<ParametroCrecimientoNinoEdadEntity>

    @Insert
    suspend fun insertAll(parametros: List<ParametroCrecimientoNinoEdadEntity>): List<Long>

    @Query("DELETE FROM parametros_crecimientos_ninos_edad")
    suspend fun deleteAll(): Int

}