package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.PacienteEntity
import com.nutrizulia.data.local.view.PacienteConConsultaYDetalles
import java.time.LocalDateTime

@Dao
interface PacienteDao {

    @Query("SELECT * FROM pacientes WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY updated_at DESC LIMIT 50")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<PacienteEntity>

    @Query("SELECT * FROM pacientes WHERE usuario_institucion_id = :usuarioInstitucionId " +
            "AND (nombres LIKE '%' || :query || '%' " +
            "OR apellidos LIKE '%' || :query || '%' " +
            "OR cedula LIKE '%' || :query || '%' " +
            "OR fecha_nacimiento LIKE '%' || :query || '%' " +
            "OR genero LIKE '%' || :query || '%') " +
            "ORDER BY updated_at DESC")
    suspend fun findAllByUsuarioInstitucionIdAndFilter(usuarioInstitucionId: Int, query: String): List<PacienteEntity>
    
    @Query("SELECT * FROM pacientes WHERE usuario_institucion_id = :usuarioInstitucionId AND cedula = :cedula")
    suspend fun findByCedula( usuarioInstitucionId: Int, cedula: String): PacienteEntity?

    @Query("SELECT * FROM pacientes WHERE id = :id AND usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findById(usuarioInstitucionId: Int, id: String): PacienteEntity?

    @Query("SELECT * FROM pacientes WHERE is_synced = 0 AND usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): List<PacienteEntity>

    @Query("SELECT COUNT(*) FROM pacientes WHERE is_synced = 0 AND usuario_institucion_id = :usuarioInstitucionId")
    suspend fun countNotSynced(usuarioInstitucionId: Int): Int

    @Upsert
    suspend fun upsert(paciente: PacienteEntity): Long

    @Upsert
    suspend fun upsertAll(pacientes: List<PacienteEntity>)

    @Insert
    suspend fun insertAll(pacientes: List<PacienteEntity>): List<Long>

    @Update
    suspend fun update(paciente: PacienteEntity): Int

    @Update
    suspend fun updateAll(pacientes: List<PacienteEntity>): Int

    @Delete
    suspend fun delete(paciente: PacienteEntity): Int

    @Query("DELETE FROM pacientes")
    suspend fun deleteAll(): Int

    @Query("UPDATE pacientes SET is_synced = 1, updated_at = :updatedAt WHERE id = :id")
    suspend fun markAsSynced(id: String, updatedAt: LocalDateTime): Int

    @Query("UPDATE pacientes SET is_synced = 1, updated_at = :updatedAt WHERE id IN (:ids)")
    suspend fun markMultipleAsSynced(ids: List<String>, updatedAt: LocalDateTime): Int

    @Query("""
        SELECT * FROM pacientes_con_consulta_y_detalles 
        WHERE pacienteId = :pacienteId 
        AND nombreCompletoPaciente LIKE '%' || :filtro || '%'
        ORDER BY fechaHoraReal DESC
    """)
    suspend fun getPacienteConsultaYDetallesByFiltro(pacienteId: String, filtro: String): List<PacienteConConsultaYDetalles>

    @Query("""
        SELECT * FROM pacientes_con_consulta_y_detalles 
        WHERE pacienteId = :pacienteId ORDER BY fechaHoraReal DESC
    """)
    suspend fun getPacienteConsultaYDetallesByPacienteId(pacienteId: String): List<PacienteConConsultaYDetalles>


}