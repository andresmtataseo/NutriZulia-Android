package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionImcEntity

@Dao
interface ReglaInterpretacionImcDao {

    @Query("SELECT * FROM reglas_interpretaciones_imc WHERE tipo_indicador_id = :tipoIndicadorId")
    suspend fun findAllByTipoIndicadorId(tipoIndicadorId: Int): List<ReglaInterpretacionImcEntity>

    @Insert
    suspend fun insertAll(reglas: List<ReglaInterpretacionImcEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(reglas: List<ReglaInterpretacionImcEntity>): List<Long>

    @Query("DELETE FROM reglas_interpretaciones_imc")
    suspend fun deleteAll(): Int

}