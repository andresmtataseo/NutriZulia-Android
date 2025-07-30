package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.EvaluacionAntropometricaEntity
import java.time.LocalDateTime

@Dao
interface EvaluacionAntropometricaDao {

    @Query("SELECT * FROM evaluaciones_antropometricas WHERE consulta_id = :consultaId")
    suspend fun findAllByConsultaId(consultaId: String): List<EvaluacionAntropometricaEntity>

    @Query("SELECT * FROM evaluaciones_antropometricas WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<EvaluacionAntropometricaEntity>

    @Insert
    suspend fun insert(evaluacionAntropometrica: EvaluacionAntropometricaEntity): Long

    @Insert
    suspend fun insertAll(evaluacionesAntropometricas: List<EvaluacionAntropometricaEntity>): List<Long>

    @Upsert
    suspend fun upsert(evaluacionAntropometrica: EvaluacionAntropometricaEntity): Long

    @Upsert
    suspend fun upsertAll(evaluacionesAntropometricas: List<EvaluacionAntropometricaEntity>)

    @Query("DELETE FROM evaluaciones_antropometricas")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(evaluacionAntropometrica: EvaluacionAntropometricaEntity): Int

}