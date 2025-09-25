package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity
import com.nutrizulia.data.local.pojo.DiagnosticoConDescripcion
import java.time.LocalDateTime

@Dao
interface DiagnosticoDao {

    @Query("SELECT * FROM diagnosticos WHERE consulta_id = :consultaId AND is_deleted = 0")
    suspend fun findByConsultaId(consultaId: String): List<DiagnosticoEntity>

    @Query("""SELECT d.* FROM diagnosticos d 
        INNER JOIN consultas c ON d.consulta_id = c.id 
        WHERE c.paciente_id = :pacienteId AND d.is_deleted = 0 
        ORDER BY c.fecha_hora_real DESC, c.fecha_hora_programada DESC""")
    suspend fun findHistoricosByPacienteId(pacienteId: String): List<DiagnosticoEntity>

    @Query("""
        SELECT d.id, d.consulta_id, d.riesgo_biologico_id, d.enfermedad_id, d.is_principal, 
               d.updated_at, d.is_deleted, d.is_synced,
               rb.nombre as riesgo_biologico_nombre,
               e.nombre as enfermedad_nombre,
               COALESCE(c.fecha_hora_real, c.fecha_hora_programada) as fecha_consulta
        FROM diagnosticos d 
        INNER JOIN consultas c ON d.consulta_id = c.id 
        LEFT JOIN riesgos_biologicos rb ON d.riesgo_biologico_id = rb.id
        LEFT JOIN enfermedades e ON d.enfermedad_id = e.id
        WHERE c.paciente_id = :pacienteId AND d.is_deleted = 0 AND c.is_deleted = 0
        ORDER BY COALESCE(c.fecha_hora_real, c.fecha_hora_programada) DESC
    """)
    suspend fun findDiagnosticosConDescripcionesByPacienteId(pacienteId: String): List<DiagnosticoConDescripcion>

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