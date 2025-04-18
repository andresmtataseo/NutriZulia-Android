package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nutrizulia.data.local.entity.PacienteEntity

@Dao
interface PacienteDao {

    @Query("SELECT * FROM pacientes ORDER BY primer_nombre ASC")
    suspend fun getAllPacientes(): List<PacienteEntity>

    @Query("SELECT * FROM pacientes WHERE cedula = :cedula")
    suspend fun getPacienteByCedula(cedula: String): PacienteEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPaciente(paciente: PacienteEntity): Long

    @Update
    suspend fun updatePaciente(paciente: PacienteEntity): Int

    @Query("DELETE FROM pacientes WHERE cedula = :cedula")
    suspend fun deletePacienteByCedula(cedula: String)

}