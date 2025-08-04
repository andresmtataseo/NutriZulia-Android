package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.PacienteRepresentanteEntity
import java.time.LocalDateTime

@Dao
interface PacienteRepresentanteDao {

    @Query("SELECT * FROM pacientes_representantes WHERE paciente_id = :pacienteId")
    suspend fun findByPacienteId(pacienteId: String): PacienteRepresentanteEntity?

    @Query("SELECT * FROM pacientes_representantes WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<PacienteRepresentanteEntity>

    @Query("SELECT COUNT(paciente_id) FROM pacientes_representantes WHERE usuario_institucion_id = :usuarioInstitucionId AND representante_id = :representanteId AND is_deleted = 0")
    suspend fun countPacienteIdByUsuarioInstitucionIdAndRepresentanteId(usuarioInstitucionId: Int, representanteId: String): Int

    @Query("SELECT * FROM pacientes_representantes WHERE is_synced = 0")
    suspend fun findAllNotSynced(): List<PacienteRepresentanteEntity>

    @Upsert
    suspend fun upsert(pacienteRepresentante: PacienteRepresentanteEntity)
    
    @Upsert
    suspend fun upsertAll(pacientesRepresentantes: List<PacienteRepresentanteEntity>)
    
    @Upsert
    suspend fun updateAll(pacientesRepresentantes: List<PacienteRepresentanteEntity>)
    
    @Insert
    suspend fun insert(pacienteRepresentante: PacienteRepresentanteEntity): Long

    @Insert
    suspend fun insertAll(pacientesRepresentantes: List<PacienteRepresentanteEntity>): List<Long>

    @Delete
    suspend fun delete(pacienteRepresentante: PacienteRepresentanteEntity): Int

    @Query("DELETE FROM pacientes_representantes")
    suspend fun deleteAll(): Int

}