package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity

@Dao
interface DiagnosticoDao {

    @Query("SELECT * FROM diagnosticos WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): List<DiagnosticoEntity>

    @Insert
    suspend fun insert(diagnostico: DiagnosticoEntity): Long

    @Insert
    suspend fun insertAll(diagnosticos: List<DiagnosticoEntity>): List<Long>

    @Delete
    suspend fun delete(diagnostico: DiagnosticoEntity): Int

    @Query("DELETE FROM diagnosticos")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM diagnosticos WHERE consulta_id = :consultaId")
    suspend fun deleteByConsultaId(consultaId: String): Int

}