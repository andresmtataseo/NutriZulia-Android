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

    @Query("SELECT * FROM diagnosticos WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): List<DiagnosticoEntity>

    @Query("SELECT * FROM diagnosticos WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<DiagnosticoEntity>

    @Query("SELECT * FROM diagnosticos WHERE is_synced = 0")
    suspend fun findAllNotSynced(): List<DiagnosticoEntity>

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

    @Query("DELETE FROM diagnosticos")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM diagnosticos WHERE consulta_id = :consultaId")
    suspend fun deleteByConsultaId(consultaId: String): Int

}