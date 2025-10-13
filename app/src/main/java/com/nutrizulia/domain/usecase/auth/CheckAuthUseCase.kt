package com.nutrizulia.domain.usecase.auth

import com.nutrizulia.data.repository.auth.AuthRepository
import javax.inject.Inject

/**
 * Use case para validar el estado de autenticación del usuario
 * mediante el endpoint protegido /auth/check.
 */
class CheckAuthUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.checkAuth()
    }
}