package com.nutrizulia.domain.usecase.auth

import com.nutrizulia.data.repository.auth.AuthRepository
import com.nutrizulia.domain.model.auth.SignIn
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(cedula: String, clave: String): Result<SignIn> {
        return try {
            val result = repository.authenticate(cedula, clave)
            if (result != null) {
                Result.success(result)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}