package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.collection.EvaluacionAntropometricaEntity

@Dao
interface EvaluacionAntropometricaDao {

    @Query("SELECT * FROM evaluaciones_antropometricas WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): EvaluacionAntropometricaEntity?

    @Insert
    suspend fun insert(evaluacionAntropometrica: EvaluacionAntropometricaEntity): Long

    @Insert
    suspend fun insertAll(evaluacionesAntropometricas: List<EvaluacionAntropometricaEntity>): List<Long>

    @Query("DELETE FROM evaluaciones_antropometricas")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(evaluacionAntropometrica: EvaluacionAntropometricaEntity): Int

}