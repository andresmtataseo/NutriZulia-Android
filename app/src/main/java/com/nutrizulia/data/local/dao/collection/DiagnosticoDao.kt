package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity
import java.time.LocalDateTime

@Dao
interface DiagnosticoDao {

    @Query("SELECT * FROM diagnosticos WHERE consulta_id = :consultaId AND is_deleted = 0")
    suspend fun findByConsultaId(consultaId: String): List<DiagnosticoEntity>

    @Query("SELECT d.* FROM diagnosticos d INNER JOIN consultas c ON d.consulta_id = c.id WHERE d.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): List<DiagnosticoEntity>

    @Query("SELECT COUNT(*) FROM diagnosticos d INNER JOIN consultas c ON d.consulta_id = c.id WHERE d.is_synced = 0 AND c.usuario_institucion_id = :usuarioInstitucionId")
    suspend fun countNotSynced(usuarioInstitucionId: Int): Int

    @Insert
    suspend fun insert(diagnostico: DiagnosticoEntity): Long

    @Insert
    suspend fun insertAll(diagnosticos: List<DiagnosticoEntity>): List<Long>

    @Upsert
    suspend fun upsert(diagnostico: DiagnosticoEntity)

    @Upsert
    suspend fun upsertAll(diagnosticos: List<DiagnosticoEntity>)

    @Delete
    suspend fun delete(diagnostico: DiagnosticoEntity): Int

    @Query("UPDATE diagnosticos SET is_synced = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: String, timestamp: LocalDateTime)

    @Query("DELETE FROM diagnosticos")
    suspend fun deleteAll(): Int

    @Query("UPDATE diagnosticos SET is_deleted = 1, is_synced = 0 WHERE consulta_id = :consultaId")
    suspend fun deleteByConsultaId(consultaId: String): Int

}