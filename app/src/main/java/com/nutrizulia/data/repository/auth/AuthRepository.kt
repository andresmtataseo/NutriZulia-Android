package com.nutrizulia.data.repository.auth

import com.nutrizulia.data.local.dao.user.UsuarioDao
import com.nutrizulia.data.local.entity.user.toEntity
import com.nutrizulia.util.TokenManager
import com.nutrizulia.data.remote.api.auth.AuthService
import com.nutrizulia.domain.model.ApiResponse
import com.nutrizulia.domain.model.auth.SignIn
import com.nutrizulia.domain.model.auth.toDomain
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthService,
    private val tokenManager: TokenManager,
    private val usuarioDao: UsuarioDao
) {

    suspend fun authenticate(cedula: String, clave: String): SignIn {
        val response = api.signIn(cedula, clave)
        val data = response.body()?.data?.toDomain() ?: throw Exception("Respuesta vacía del servidor")
        tokenManager.saveToken(data.token)
        usuarioDao.insert(data.usuario.toEntity())
        return data
    }

    suspend fun forgotPassword(cedula: String): ApiResponse<Any>  {
        val response = api.forgotPassword(cedula)
        return response.body()?.toDomain() ?: throw Exception("Respuesta vacía del servidor")
    }

    suspend fun changePassword(claveActual: String, claveNueva: String, claveNuevaConfirmacion: String): ApiResponse<Any> {
        val response = api.changePassword(claveActual, claveNueva, claveNuevaConfirmacion)
        return response.body()?.toDomain() ?: throw Exception("Respuesta vacía del servidor")
    }

    suspend fun checkAuth(): Boolean {
        val response = api.checkAuth()
        return response.isSuccessful
    }

}
