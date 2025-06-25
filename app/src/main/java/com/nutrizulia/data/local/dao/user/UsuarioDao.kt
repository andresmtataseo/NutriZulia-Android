package com.nutrizulia.data.local.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.user.UsuarioEntity

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios")
    suspend fun findAll(): List<UsuarioEntity>

    @Insert
    suspend fun insertAll(usuarios: List<UsuarioEntity>): List<Long>

    @Query("DELETE FROM usuarios")
    suspend fun deleteAll(): Int

}