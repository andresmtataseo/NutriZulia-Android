package com.nutrizulia.data.remote.api.user

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.user.SaveEmailRequestDto
import com.nutrizulia.data.remote.dto.user.SavePhoneRequestDto
import retrofit2.Response
import javax.inject.Inject

class UserService @Inject constructor(
    private val api: IUserService
) {
    suspend fun checkPhoneNumber(phoneNumber: String): Response<Boolean> {
        return api.checkPhoneNumber(phoneNumber)
    }
    
    suspend fun checkEmail(email: String): Response<Boolean> {
        return api.checkEmail(email)
    }
    
    suspend fun savePhone(idUsuario: Int, telefono: String): Response<ApiResponseDto<Void>> {
        val request = SavePhoneRequestDto(idUsuario = idUsuario, telefono = telefono)
        return api.savePhone(request)
    }
    
    suspend fun saveEmail(idUsuario: Int, correo: String): Response<ApiResponseDto<Void>> {
        val request = SaveEmailRequestDto(idUsuario = idUsuario, correo = correo)
        return api.saveEmail(request)
    }
}