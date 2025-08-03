package com.nutrizulia.domain.usecase.auth

import com.nutrizulia.data.repository.auth.AuthRepository
import com.nutrizulia.domain.model.ApiResponse
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(cedula: String): ApiResponse<Any> {
        return repository.forgotPassword(cedula)
    }
}