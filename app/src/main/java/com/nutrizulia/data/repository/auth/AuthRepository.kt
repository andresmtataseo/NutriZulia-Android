package com.nutrizulia.data.repository.auth

import com.nutrizulia.data.preferences.TokenManager
import com.nutrizulia.data.remote.api.auth.AuthService
import com.nutrizulia.domain.model.auth.SignIn
import com.nutrizulia.domain.model.auth.toDomain
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthService,
    private val tokenManager: TokenManager
) {

    suspend fun authenticate(cedula: String, clave: String): SignIn? {
        val response = api.signIn(cedula, clave)
        val body = response.body()?.toDomain()
        body?.token?.let {
            tokenManager.saveToken(it)
        }
        return body
    }

}