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

    @Query("""
        SELECT ea.* FROM evaluaciones_antropometricas ea 
        INNER JOIN consultas c ON ea.consulta_id = c.id 
        WHERE c.paciente_id = :pacienteId 
        AND ea.tipo_indicador_id = :tipoIndicadorId 
        AND ea.is_deleted = 0 
        AND c.is_deleted = 0
        ORDER BY ea.fecha_evaluacion DESC, ea.updated_at DESC 
        LIMIT 1
    """)
    suspend fun findLatestByPacienteIdAndTipoIndicador(pacienteId: String, tipoIndicadorId: Int): EvaluacionAntropometricaEntity?

    @Query("""
        SELECT DISTINCT ea.tipo_indicador_id FROM evaluaciones_antropometricas ea 
        INNER JOIN consultas c ON ea.consulta_id = c.id 
        WHERE c.paciente_id = :pacienteId 
        AND ea.is_deleted = 0 
        AND c.is_deleted = 0
    """)
    suspend fun findDistinctTipoIndicadorIdsByPacienteId(pacienteId: String): List<Int>

    @Query("""
        SELECT ea.* FROM evaluaciones_antropometricas ea 
        INNER JOIN consultas c ON ea.consulta_id = c.id 
        WHERE ea.consulta_id = (
            SELECT c2.id FROM consultas c2 
            INNER JOIN evaluaciones_antropometricas ea2 ON c2.id = ea2.consulta_id 
            WHERE c2.paciente_id = :pacienteId 
            AND ea2.is_deleted = 0 
            AND c2.is_deleted = 0
            ORDER BY COALESCE(c2.fecha_hora_real, c2.fecha_hora_programada) DESC 
            LIMIT 1
        )
        AND ea.is_deleted = 0
        ORDER BY ea.tipo_indicador_id ASC
    """)
    suspend fun findAllByLatestConsultaWithAntropometricData(pacienteId: String): List<EvaluacionAntropometricaEntity>

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