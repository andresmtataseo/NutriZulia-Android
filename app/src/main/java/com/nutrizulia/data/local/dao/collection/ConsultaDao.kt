package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nutrizulia.data.local.entity.collection.ConsultaEntity

@Dao
interface ConsultaDao {

    @Query("SELECT * FROM consultas WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY fecha_programada DESC")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<ConsultaEntity>

    @Insert
    suspend fun insertAll(consultas: List<ConsultaEntity>): List<Long>

    @Insert
    suspend fun insert(consulta: ConsultaEntity): Long

    @Update
    suspend fun update(consulta: ConsultaEntity): Int

    @Query("DELETE FROM consultas")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(consulta: ConsultaEntity): Int

}