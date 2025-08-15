package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionZScoreEntity

@Dao
interface ReglaInterpretacionZScoreDao {

    @Query("SELECT diagnostico FROM reglas_interpretaciones_z_score WHERE tipo_indicador_id = :tipoIndicadorId AND :zScore BETWEEN z_score_minimo AND z_score_maximo")
    suspend fun findInterpretacion(tipoIndicadorId: Int, zScore: Double): String?

    @Insert
    suspend fun insertAll(reglas: List<ReglaInterpretacionZScoreEntity>): List<Long>

    @Transaction


    @Upsert
    suspend fun upsertAll(reglas: List<ReglaInterpretacionZScoreEntity>): List<Long>

    @Query("DELETE FROM reglas_interpretaciones_z_score")
    suspend fun deleteAll(): Int

}