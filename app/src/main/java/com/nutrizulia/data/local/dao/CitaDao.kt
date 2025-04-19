package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutrizulia.data.local.entity.CitaEntity

@Dao
interface CitaDao {

    @Query("SELECT * FROM citas WHERE paciente_id = :pacienteId")
    suspend fun getCitasByPacienteId(pacienteId: Int): List<CitaEntity>

    @Query("SELECT * FROM citas")
    suspend fun getAllCitas(): List<CitaEntity>

    @Query("SELECT * FROM citas WHERE estado = 'PENDIENTE'")
    suspend fun getCitasPendientes(): List<CitaEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCita(cita: CitaEntity): Long

    @Query("UPDATE citas SET estado = 'CANCELADA' WHERE id = :citaId")
    suspend fun cancelarCita(citaId: Int)

    @Query("UPDATE citas SET estado = 'FINALIZADA' WHERE id = :citaId")
    suspend fun finalizarCita(citaId: Int)

    @Query("UPDATE citas SET fecha_programada = :fechaProgramada, hora_programada = :horaProgramada WHERE id = :citaId")
    suspend fun actualizarCita(citaId: Int, fechaProgramada: String, horaProgramada: String)


}