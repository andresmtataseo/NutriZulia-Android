package com.nutrizulia.domain.usecase.auth

import com.nutrizulia.data.repository.auth.AuthRepository
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.TokenManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke() {
        // Ejecutar logout en servidor en segundo plano (no bloquear ni interrumpir)
        try {
            // Lanzar sin bloquear el flujo de limpieza local
            authRepository.logoutInBackground()
        } catch (_: Exception) {
            // Silenciar errores
        }
        // Limpiar siempre la sesión local
        sessionManager.clearCurrentInstitution()
        tokenManager.clearToken()
    }
}