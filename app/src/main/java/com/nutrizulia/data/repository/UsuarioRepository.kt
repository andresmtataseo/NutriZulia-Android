package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.UsuarioDao
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.domain.model.Usuario
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val  usuarioDao: UsuarioDao
){
    suspend fun getUsuarioByCedulaAndClave(cedula: String, clave: String): Usuario? {
        return usuarioDao.getUsuarioByCedulaAndClave(cedula, clave)?.toDomain()
    }

    suspend fun insertUsuario(usuario: Usuario): Long {
        return usuarioDao.insertUsuario(usuario.toEntity())
    }
}