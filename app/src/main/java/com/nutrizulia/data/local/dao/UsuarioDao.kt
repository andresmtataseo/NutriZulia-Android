package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE cedula = :cedula AND clave = :clave")
    suspend fun getUsuarioByCedulaAndClave(cedula: String, clave: String): UsuarioEntity?

    @Query("SELECT correo FROM usuarios WHERE cedula = :cedula")
    suspend fun getCorreoByCedula(cedula: String): String?

    @Insert
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

}