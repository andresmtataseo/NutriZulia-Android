package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionPercentilEntity

@Dao
interface ReglaInterpretacionPercentilDao {

    @Query("SELECT * FROM reglas_interpretaciones_percentil WHERE tipo_indicador_id = :tipoIndicadorId")
    suspend fun findAllByTipoIndicadorId(tipoIndicadorId: Int): List<ReglaInterpretacionPercentilEntity>

    @Insert
    suspend fun insertAll(reglas: List<ReglaInterpretacionPercentilEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(reglas: List<ReglaInterpretacionPercentilEntity>): List<Long>

    @Query("DELETE FROM reglas_interpretaciones_percentil")
    suspend fun deleteAll(): Int

}