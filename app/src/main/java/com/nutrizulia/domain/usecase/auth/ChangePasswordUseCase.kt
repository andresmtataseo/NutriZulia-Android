package com.nutrizulia.domain.usecase.auth

import com.nutrizulia.data.repository.auth.AuthRepository
import com.nutrizulia.domain.model.ApiResponse
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(claveActual: String, claveNueva: String, claveNuevaConfirmacion: String): ApiResponse<Any> {
        return repository.changePassword(claveActual, claveNueva, claveNuevaConfirmacion)
    }
}