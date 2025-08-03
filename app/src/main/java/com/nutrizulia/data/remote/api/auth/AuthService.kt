package com.nutrizulia.data.remote.api.auth

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.auth.ChangePasswordRequestDto
import com.nutrizulia.data.remote.dto.auth.CheckAuthResponseDto
import com.nutrizulia.data.remote.dto.auth.ForgotPasswordRequestDto
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

    suspend fun forgotPassword(cedula: String): Response<ApiResponseDto<Any>> {
        val request = ForgotPasswordRequestDto(cedula = cedula)
        return api.forgotPassword(request)
    }

    suspend fun changePassword(claveActual: String, claveNueva: String, claveNuevaConfirmacion: String): Response<ApiResponseDto<Any>> {
        val request = ChangePasswordRequestDto(claveActual = claveActual, claveNueva = claveNueva, claveNuevaConfirmacion = claveNuevaConfirmacion)
        return api.changePassword(request)
    }

    suspend fun checkAuth(): Response<ApiResponseDto<CheckAuthResponseDto>> {
        return api.checkAuth()
    }
}