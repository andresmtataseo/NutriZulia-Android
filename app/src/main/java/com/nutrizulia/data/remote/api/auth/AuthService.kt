package com.nutrizulia.data.remote.api.auth

import com.nutrizulia.data.remote.dto.auth.SignInRequestDto
import com.nutrizulia.data.remote.dto.auth.SignInResponseDto
import retrofit2.Response
import javax.inject.Inject

class AuthService @Inject constructor(
    private val api: IAuthService
) {

    suspend fun signIn(cedula: String, clave: String): Response<SignInResponseDto> {
        val request = SignInRequestDto(cedula = cedula, clave = clave)
        return api.signIn(request)
    }
}