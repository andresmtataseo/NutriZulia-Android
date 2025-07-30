package com.nutrizulia.data.local.dao.collection

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.collection.RepresentanteEntity
import java.time.LocalDateTime

@Dao
interface RepresentanteDao {

    @Query("SELECT * FROM representantes WHERE usuario_institucion_id = :usuarioInstitucionId ORDER BY updated_at DESC LIMIT 50")
    suspend fun findAll(usuarioInstitucionId: Int): List<RepresentanteEntity>

    @Query("SELECT * FROM representantes WHERE usuario_institucion_id = :usuarioInstitucionId " +
            "AND (nombres LIKE '%' || :query || '%' " +
            "OR apellidos LIKE '%' || :query || '%' " +
            "OR cedula LIKE '%' || :query || '%' " +
            "OR fecha_nacimiento LIKE '%' || :query || '%' " +
            "OR genero LIKE '%' || :query || '%') " +
            "ORDER BY updated_at DESC")
    suspend fun findAllByUsuarioInstitucionIdAndFilter(usuarioInstitucionId: Int, query: String): List<RepresentanteEntity>

    @Upsert
    suspend fun upsert(representante: RepresentanteEntity)

    @Query("SELECT * FROM representantes WHERE usuario_institucion_id = :usuarioInstitucionId AND cedula = :cedula")
    suspend fun findByCedula(usuarioInstitucionId: Int, cedula: String): RepresentanteEntity?

    @Query("SELECT * FROM representantes WHERE usuario_institucion_id = :usuarioInstitucionId AND id = :representanteId")
    suspend fun findById(usuarioInstitucionId: Int, representanteId: String): RepresentanteEntity?

    // Consultas para sincronización
    @Query("SELECT * FROM representantes WHERE updated_at > :timestamp")
    suspend fun findPendingChanges(timestamp: LocalDateTime): List<RepresentanteEntity>

    @Upsert
    suspend fun upsertAll(representantes: List<RepresentanteEntity>)

}