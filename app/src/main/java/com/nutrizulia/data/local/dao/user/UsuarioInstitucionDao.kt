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

    @Query("SELECT * FROM usuarios_instituciones WHERE usuario_id = :usuarioId")
    suspend fun findByUsuarioId(usuarioId: Int): List<UsuarioInstitucionEntity>

    @Query("DELETE FROM usuarios_instituciones WHERE id = :id")
    suspend fun deleteById(id: Int)
    @Upsert
    suspend fun insertAll(usuariosInstituciones: List<UsuarioInstitucionEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(userInstitutions: List<UsuarioInstitucionEntity>)


}