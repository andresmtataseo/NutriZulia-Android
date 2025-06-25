package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.collection.DetalleAntropometricoEntity

@Dao
interface DetalleAntropometricoDao {

    @Query("SELECT * FROM detalles_antropometricos WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): DetalleAntropometricoEntity?

    @Insert
    suspend fun insert(detalleAntropometrico: DetalleAntropometricoEntity): Long

    @Insert
    suspend fun insertAll(detallesAntropometricos: List<DetalleAntropometricoEntity>): List<Long>

    @Delete
    suspend fun delete(detalleAntropometrico: DetalleAntropometricoEntity): Int

    @Query("DELETE FROM detalles_antropometricos")
    suspend fun deleteAll(): Int

}