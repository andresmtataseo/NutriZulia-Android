package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.collection.PacienteEntity

@Dao
interface PacienteDao {

    @Query("SELECT * FROM pacientes WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY updated_at DESC")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<PacienteEntity>

    @Insert
    suspend fun insert(paciente: PacienteEntity): Long

    @Insert
    suspend fun insertAll(pacientes: List<PacienteEntity>): List<Long>

    @Delete
    suspend fun delete(paciente: PacienteEntity): Int

    @Query("DELETE FROM pacientes")
    suspend fun deleteAll(): Int

}