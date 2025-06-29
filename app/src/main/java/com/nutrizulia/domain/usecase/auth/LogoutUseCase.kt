package com.nutrizulia.domain.usecase.auth

import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.TokenManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val tokenManager: TokenManager
) {

    suspend operator fun invoke() {
        sessionManager.clearCurrentInstitution()
        tokenManager.clearToken()
    }

}