package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.ConsultaEntity
import com.nutrizulia.data.local.enum.Estado

@Dao
interface ConsultaDao {

    @Query("SELECT * FROM consultas WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY fecha_hora_programada ASC")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<ConsultaEntity>

    @Query("SELECT * FROM consultas WHERE paciente_id = :pacienteId AND estado = 'PENDIENTE' OR estado = 'REPROGRAMADA'")
    suspend fun findConsultaProgramadaByPacienteId(pacienteId: String): ConsultaEntity?

    @Query("SELECT * FROM consultas WHERE id = :id")
    suspend fun findConsultaProgramadaById(id: String): ConsultaEntity?

    @Insert
    suspend fun insertAll(consultas: List<ConsultaEntity>): List<Long>

    @Upsert
    suspend fun upsert(consulta: ConsultaEntity): Long

    @Query("UPDATE consultas SET estado = :estado WHERE id = :id")
    suspend fun updateEstadoById(id: String, estado: Estado)
    @Update
    suspend fun update(consulta: ConsultaEntity): Int

    @Query("DELETE FROM consultas")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(consulta: ConsultaEntity): Int

}