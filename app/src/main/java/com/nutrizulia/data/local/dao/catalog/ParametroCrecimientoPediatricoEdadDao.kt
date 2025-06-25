package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoPediatricoEdadEntity

@Dao
interface ParametroCrecimientoPediatricoEdadDao {

    @Query("SELECT * FROM parametros_crecimientos_pediatricos_edad WHERE tipo_indicador_id = :tipoIndicadorId AND grupo_etario_id = :grupoEtarioId AND genero = :genero AND edad_dia = :edadDia")
    suspend fun findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndEdadMes(
        tipoIndicadorId: Int,
        grupoEtarioId: Int,
        genero: String,
        edadDia: Int
    ): ParametroCrecimientoPediatricoEdadEntity?

    @Insert
    suspend fun insertAll(parametros: List<ParametroCrecimientoPediatricoEdadEntity>): List<Long>

    @Query("DELETE FROM parametros_crecimientos_pediatricos_edad")
    suspend fun deleteAll(): Int


}