package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionImcEntity

@Dao
interface ReglaInterpretacionImcDao {

    @Query("SELECT diagnostico FROM reglas_interpretaciones_imc WHERE tipo_indicador_id = :tipoIndicadorId AND :imc BETWEEN imc_minimo AND imc_maximo")
    suspend fun findInterpretacionByTipoIndicadorIdAndImc(tipoIndicadorId: Int, imc: Double): String?

    @Insert
    suspend fun insertAll(reglas: List<ReglaInterpretacionImcEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(reglas: List<ReglaInterpretacionImcEntity>): List<Long>

    @Query("DELETE FROM reglas_interpretaciones_imc")
    suspend fun deleteAll(): Int

}