package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.collection.DetalleMetabolicoEntity

@Dao
interface DetalleMetabolicoDao {

    @Query("SELECT * FROM detalles_metabolicos WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): DetalleMetabolicoEntity?

    @Insert
    suspend fun insert(detalleMetabolico: DetalleMetabolicoEntity): Long

    @Insert
    suspend fun insertAll(detallesMetabolicos: List<DetalleMetabolicoEntity>): List<Long>

    @Delete
    suspend fun delete(detalleMetabolico: DetalleMetabolicoEntity): Int

    @Query("DELETE FROM detalles_metabolicos")
    suspend fun deleteAll(): Int

}