package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.collection.DetalleVitalEntity

@Dao
interface DetalleVitalDao {

    @Query("SELECT * FROM detalles_vitales WHERE consulta_id = :consultaId")
    suspend fun findByConsultaId(consultaId: String): DetalleVitalEntity?

    @Insert
    suspend fun insert(detalleVital: DetalleVitalEntity): Long

    @Insert
    suspend fun insertAll(detallesVitales: List<DetalleVitalEntity>): List<Long>

    @Query("DELETE FROM detalles_vitales")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(detalleVital: DetalleVitalEntity): Int

}