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

    @Query("SELECT * FROM evaluaciones_antropometricas WHERE consulta_id = :consultaId AND is_deleted = 0")
    suspend fun findAllByConsultaId(consultaId: String): List<EvaluacionAntropometricaEntity>

    @Query("SELECT ea.* FROM evaluaciones_antropometricas ea INNER JOIN consultas c ON ea.consulta_id = c.id WHERE ea.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): List<EvaluacionAntropometricaEntity>

    @Query("SELECT COUNT(*) FROM evaluaciones_antropometricas ea INNER JOIN consultas c ON ea.consulta_id = c.id WHERE ea.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun countNotSynced(usuarioInstitucionId: Int): Int

    @Insert
    suspend fun insert(evaluacionAntropometrica: EvaluacionAntropometricaEntity): Long

    @Insert
    suspend fun insertAll(evaluacionesAntropometricas: List<EvaluacionAntropometricaEntity>): List<Long>

    @Upsert
    suspend fun upsert(evaluacionAntropometrica: EvaluacionAntropometricaEntity): Long

    @Upsert
    suspend fun upsertAll(evaluacionesAntropometricas: List<EvaluacionAntropometricaEntity>)

    @Query("UPDATE evaluaciones_antropometricas SET is_synced = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: String, timestamp: LocalDateTime)

    @Query("DELETE FROM evaluaciones_antropometricas")
    suspend fun deleteAll(): Int

    @Query("UPDATE evaluaciones_antropometricas SET is_deleted = 1, is_synced = 0 WHERE consulta_id = :consultaId")
    suspend fun deleteByConsultaId(consultaId: String): Int

    @Delete
    suspend fun delete(evaluacionAntropometrica: EvaluacionAntropometricaEntity): Int

}