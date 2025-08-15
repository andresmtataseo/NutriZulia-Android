package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoPediatricoLongitudEntity

@Dao
interface ParametroCrecimientoPediatricoLongitudDao {

    @Query("SELECT * FROM parametros_crecimientos_pediatricos_longitud WHERE tipo_indicador_id = :tipoIndicadorId AND grupo_etario_id = :grupoEtarioId AND genero = :genero AND longitud_cm = :longitudCm AND tipo_medicion = :tipoMedicion")
    suspend fun findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndLongitudCmAndTipoMedicion(
        tipoIndicadorId: Int,
        grupoEtarioId: Int,
        genero: String,
        longitudCm: Double,
        tipoMedicion: String
    ): ParametroCrecimientoPediatricoLongitudEntity?

    @Query("""
    SELECT * 
    FROM parametros_crecimientos_pediatricos_longitud 
    WHERE grupo_etario_id = :grupoEtarioId 
        AND genero = :genero 
        AND longitud_cm BETWEEN :minLongitud AND :maxLongitud
        AND tipo_medicion = :tipoMedicion
    """)
    suspend fun findAllByGrupoEtarioIdAndGeneroAndLongitudCmAndTipoMedicion(
        grupoEtarioId: Int,
        genero: String,
        minLongitud: Double,
        maxLongitud: Double,
        tipoMedicion: String
    ): List<ParametroCrecimientoPediatricoLongitudEntity>

    @Insert
    suspend fun insertAll(parametros: List<ParametroCrecimientoPediatricoLongitudEntity>): List<Long>

    @Transaction


    @Upsert
    suspend fun upsertAll(parametros: List<ParametroCrecimientoPediatricoLongitudEntity>): List<Long>

    @Query("DELETE FROM parametros_crecimientos_pediatricos_longitud")
    suspend fun deleteAll(): Int

}