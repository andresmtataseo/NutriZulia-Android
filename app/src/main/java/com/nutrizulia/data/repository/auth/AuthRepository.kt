package com.nutrizulia.data.repository.auth

import com.nutrizulia.data.local.dao.user.UsuarioDao
import com.nutrizulia.data.local.entity.user.toEntity
import com.nutrizulia.util.TokenManager
import com.nutrizulia.data.remote.api.auth.AuthService
import com.nutrizulia.domain.model.auth.SignIn
import com.nutrizulia.domain.model.auth.toDomain
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthService,
    private val tokenManager: TokenManager,
    private val usuarioDao: UsuarioDao
) {

    suspend fun authenticate(cedula: String, clave: String): SignIn {
        val response = api.signIn(cedula, clave)
        val body = response.body()?.toDomain() ?: throw Exception("Respuesta vacía del servidor")
        tokenManager.saveToken(body.token)
        usuarioDao.insert(body.usuario.toEntity())
        return body
    }

}
