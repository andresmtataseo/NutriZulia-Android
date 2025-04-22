package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.nutrizulia.data.local.dto.CitaConPacienteDto
import com.nutrizulia.data.local.entity.CitaEntity

@Dao
interface CitaDao {

    @Query("SELECT * FROM citas WHERE paciente_id = :pacienteId")
    suspend fun getCitasByPacienteId(pacienteId: Int): List<CitaEntity>

    @Query("SELECT * FROM citas")
    suspend fun getAllCitas(): List<CitaEntity>

    @Transaction
    @Query("SELECT * FROM citas")
    suspend fun getAllCitasConPacientes(): List<CitaConPacienteDto>

    @Transaction
    @Query("SELECT * FROM citas WHERE id = :idCita")
    suspend fun getCitaConPaciente(idCita: Int): CitaConPacienteDto

    @Query("SELECT * FROM citas WHERE estado = 'PENDIENTE'")
    suspend fun getCitasPendientes(): List<CitaEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCita(cita: CitaEntity): Long

    @Query("UPDATE citas SET estado = :nuevoEstado WHERE id = :citaId")
    suspend fun actualizarEstadoCita(citaId: Int, nuevoEstado: String): Int

    @Query("UPDATE citas SET fecha_programada = :fechaProgramada, hora_programada = :horaProgramada WHERE id = :citaId")
    suspend fun actualizarFechaCita(citaId: Int, fechaProgramada: String, horaProgramada: String)


}