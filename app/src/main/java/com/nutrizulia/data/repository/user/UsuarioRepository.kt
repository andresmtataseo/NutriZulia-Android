package com.nutrizulia.data.repository.user

import com.nutrizulia.data.local.dao.user.UsuarioDao
import com.nutrizulia.data.remote.api.user.UserService
import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.domain.model.user.Usuario
import com.nutrizulia.domain.model.user.toDomain
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val dao: UsuarioDao,
    private val api: UserService
){
    suspend fun findById(id: Int) : Usuario? {
        return dao.findById(id)?.toDomain()
    }
    
    suspend fun checkPhoneNumber(phoneNumber: String) : Boolean {
        val response = api.checkPhoneNumber(phoneNumber)
        if (response.isSuccessful) {
            return response.body() ?: false
        }
        return false
    }
    
    suspend fun checkEmail(email: String) : Boolean {
        val response = api.checkEmail(email)
        if (response.isSuccessful) {
            return response.body() ?: false
        }
        return false
    }
    
    suspend fun savePhone(idUsuario: Int, telefono: String): Result<ApiResponseDto<Void>> {
        return try {
            val response = api.savePhone(idUsuario, telefono)
            if (response.isSuccessful) {
                response.body()?.let { 
                    Result.success(it) 
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                Result.failure(Exception("Error en la respuesta: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveEmail(idUsuario: Int, correo: String): Result<ApiResponseDto<Void>> {
        return try {
            val response = api.saveEmail(idUsuario, correo)
            if (response.isSuccessful) {
                response.body()?.let { 
                    Result.success(it) 
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                Result.failure(Exception("Error en la respuesta: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}