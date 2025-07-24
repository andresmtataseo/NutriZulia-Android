package com.nutrizulia.domain.usecase.user

import com.nutrizulia.util.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetCurrentInstitutionIdUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(): Int? {
        return sessionManager.currentInstitutionIdFlow.firstOrNull()
    }
}