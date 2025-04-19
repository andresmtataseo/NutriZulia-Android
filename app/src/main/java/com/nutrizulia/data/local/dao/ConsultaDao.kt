package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutrizulia.data.local.entity.ConsultaEntity

@Dao
interface ConsultaDao {

    @Query("SELECT * FROM consultas WHERE paciente_id = :pacienteId")
    suspend fun getConsultasByPacienteId(pacienteId: Int): List<ConsultaEntity>

    @Query("SELECT * FROM consultas")
    suspend fun getAllConsultas(): List<ConsultaEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertConsulta(consulta: ConsultaEntity): Long

}