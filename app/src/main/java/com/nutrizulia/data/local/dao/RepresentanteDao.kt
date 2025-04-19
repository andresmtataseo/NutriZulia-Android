package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutrizulia.data.local.entity.RepresentanteEntity

@Dao
interface RepresentanteDao {

    @Query("SELECT * FROM representantes WHERE paciente_id = :pacienteId")
    suspend fun getRepresentanteByPacienteId(pacienteId: Int): RepresentanteEntity

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRepresentante(representante: RepresentanteEntity): Long


}