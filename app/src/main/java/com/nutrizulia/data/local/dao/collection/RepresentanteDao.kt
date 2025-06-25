package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.collection.PacienteRepresentanteEntity

@Dao
interface RepresentanteDao {

    @Query("SELECT * FROM pacientes_representantes WHERE paciente_id = :pacienteId")
    suspend fun findByPacienteId(pacienteId: Int): PacienteRepresentanteEntity?

    @Insert
    suspend fun insert(pacienteRepresentante: PacienteRepresentanteEntity): Long

    @Insert
    suspend fun insertAll(pacientesRepresentantes: List<PacienteRepresentanteEntity>): List<Long>

    @Delete
    suspend fun delete(pacienteRepresentante: PacienteRepresentanteEntity): Int

    @Query("DELETE FROM pacientes_representantes")
    suspend fun deleteAll(): Int

}