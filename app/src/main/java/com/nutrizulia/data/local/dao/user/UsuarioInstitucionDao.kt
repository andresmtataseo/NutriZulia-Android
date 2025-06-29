package com.nutrizulia.data.local.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.user.UsuarioInstitucionEntity

@Dao
interface UsuarioInstitucionDao {

    @Query("SELECT * FROM usuarios_instituciones WHERE usuario_id = :usuarioId")
    suspend fun findAllByUsuarioId(usuarioId: Int): List<UsuarioInstitucionEntity>

    @Upsert
    suspend fun insertAll(usuariosInstituciones: List<UsuarioInstitucionEntity>): List<Long>

    @Query("DELETE FROM usuarios_instituciones")
    suspend fun deleteAll(): Int

}