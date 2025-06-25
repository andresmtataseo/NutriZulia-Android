package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.collection.DetallePediatricoEntity

@Dao
interface DetallePediatricoDao {

    @Query("SELECT * FROM detalles_pediatricos WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): DetallePediatricoEntity?

    @Insert
    suspend fun insert(detallePediatrico: DetallePediatricoEntity): Long

    @Insert
    suspend fun insertAll(detallesPediatricos: List<DetallePediatricoEntity>): List<Long>

    @Query("DELETE FROM detalles_pediatricos")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(detallePediatrico: DetallePediatricoEntity): Int

}