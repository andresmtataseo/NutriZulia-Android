package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionZScoreEntity

@Dao
interface ReglaInterpretacionZScoreDao {

    @Query("SELECT * FROM reglas_interpretaciones_z_score WHERE tipo_indicador_id = :tipoIndicadorId")
    suspend fun findAllByTipoIndicadorId(tipoIndicadorId: Int): List<ReglaInterpretacionZScoreEntity>

    @Insert
    suspend fun insertAll(reglas: List<ReglaInterpretacionZScoreEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(reglas: List<ReglaInterpretacionZScoreEntity>): List<Long>

    @Query("DELETE FROM reglas_interpretaciones_z_score")
    suspend fun deleteAll(): Int

}