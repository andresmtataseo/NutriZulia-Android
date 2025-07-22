package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoPediatricoEdadEntity

@Dao
interface ParametroCrecimientoPediatricoEdadDao {

    @Query("SELECT * FROM parametros_crecimientos_pediatricos_edad WHERE grupo_etario_id = :grupoEtarioId AND genero = :genero AND edad_dia = :edadDia")
    suspend fun findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndEdadMes(
        grupoEtarioId: Int,
        genero: String,
        edadDia: Int
    ): ParametroCrecimientoPediatricoEdadEntity?

    @Query("SELECT * FROM parametros_crecimientos_pediatricos_edad WHERE grupo_etario_id = :grupoEtarioId AND genero = :genero AND edad_dia = :edadDia")
    suspend fun findAllByGrupoEtarioIdAndGeneroAndEdadMes(
        grupoEtarioId: Int,
        genero: String,
        edadDia: Int
    ): List<ParametroCrecimientoPediatricoEdadEntity>
    @Insert
    suspend fun insertAll(parametros: List<ParametroCrecimientoPediatricoEdadEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(parametros: List<ParametroCrecimientoPediatricoEdadEntity>): List<Long>

    @Query("DELETE FROM parametros_crecimientos_pediatricos_edad")
    suspend fun deleteAll(): Int


}