package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.ConsultaEntity

@Dao
interface ConsultaDao {

    @Query("SELECT * FROM consultas WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY fecha_hora_programada DESC")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<ConsultaEntity>

    @Query("SELECT * FROM consultas WHERE paciente_id = :pacienteId AND estado = 'PENDIENTE'")
    suspend fun findConsultaProgramadaByPacienteId(pacienteId: String): ConsultaEntity?

    @Query("SELECT * FROM consultas WHERE id = :id")
    suspend fun findConsultaProgramadaById(id: String): ConsultaEntity?

    @Insert
    suspend fun insertAll(consultas: List<ConsultaEntity>): List<Long>

    @Upsert
    suspend fun upsert(consulta: ConsultaEntity): Long

    @Update
    suspend fun update(consulta: ConsultaEntity): Int

    @Query("DELETE FROM consultas")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(consulta: ConsultaEntity): Int

}