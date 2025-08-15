package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.ActividadEntity
import com.nutrizulia.data.local.entity.collection.ConsultaEntity
import com.nutrizulia.data.local.view.ActividadConTipo
import java.time.LocalDateTime

@Dao
interface ActividadDao {

    @Query("SELECT * FROM actividades_con_tipos WHERE usuarioInstitucionId = :usuarioInstitucionId ORDER BY fechaActividad ASC")
    suspend fun findAllByUsuarioInstitucionId(usuarioInstitucionId: Int): List<ActividadConTipo>

    @Query("SELECT * FROM actividades_con_tipos WHERE usuarioInstitucionId = :usuarioInstitucionId AND (direccionActividad LIKE '%' || :filtro || '%' OR descripcionGeneralActividad LIKE '%' || :filtro || '%' OR temaPrincipalActividad LIKE '%' || :filtro || '%') ORDER BY fechaActividad ASC")
    suspend fun findAllByUsuarioInstitucionIdAndFilter(usuarioInstitucionId: Int, filtro: String): List<ActividadConTipo>

    @Query("SELECT * FROM actividades WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<ActividadEntity>

    @Insert
    suspend fun insertAll(actividades: List<ActividadEntity>): List<Long>

    @Insert
    suspend fun insert(actividad: ActividadEntity): Long

    @Query("SELECT COUNT(*) FROM actividades WHERE is_synced = 0 AND usuario_institucion_id = :usuarioInstitucionId")
    suspend fun countNotSynced(usuarioInstitucionId: Int): Int

    @Update
    suspend fun update(actividad: ActividadEntity): Int

    @Upsert
    suspend fun upsert(actividad: ActividadEntity)

    @Upsert
    suspend fun upsertAll(actividades: List<ActividadEntity>)

    @Query("SELECT * FROM actividades WHERE is_synced = 0 AND usuario_institucion_id = :usuarioInstitucionId")
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): List<ActividadEntity>

    @Query("UPDATE actividades SET is_synced = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: String, timestamp: LocalDateTime)

    @Query("DELETE FROM actividades")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(actividad: ActividadEntity): Int
}