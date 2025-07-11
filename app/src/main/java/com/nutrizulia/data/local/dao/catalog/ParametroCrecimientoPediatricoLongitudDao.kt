package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoPediatricoLongitudEntity

@Dao
interface ParametroCrecimientoPediatricoLongitudDao {

    @Query("SELECT * FROM parametros_crecimientos_pediatricos_longitud WHERE tipo_indicador_id = :tipoIndicadorId AND grupo_etario_id = :grupoEtarioId AND genero = :genero AND longitud_cm = :longitudCm AND tipo_medicion = :tipoMedicion")
    suspend fun findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndLongitudCmAndTipoMedicion(
        tipoIndicadorId: Int,
        grupoEtarioId: Int,
        genero: String,
        longitudCm: Int,
        tipoMedicion: String
    ): ParametroCrecimientoPediatricoLongitudEntity?

    @Insert
    suspend fun insertAll(parametros: List<ParametroCrecimientoPediatricoLongitudEntity>): List<Long>

    @Query("DELETE FROM parametros_crecimientos_pediatricos_longitud")
    suspend fun deleteAll(): Int

}