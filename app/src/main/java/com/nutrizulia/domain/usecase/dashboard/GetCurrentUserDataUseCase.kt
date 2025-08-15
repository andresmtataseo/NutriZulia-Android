package com.nutrizulia.domain.usecase.dashboard

import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.data.repository.user.UsuarioInstitucionRepository
import com.nutrizulia.util.JwtUtils
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetCurrentUserDataUseCase @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
    private val usuarioInstitucionRepository: UsuarioInstitucionRepository
) {
    suspend operator fun invoke(): CurrentUserDataResult {
        val token = tokenManager.getToken() ?: return CurrentUserDataResult.NotAuthenticated
        val usuarioId = JwtUtils.extractIdUsuario(token) ?: return CurrentUserDataResult.InvalidToken
        val currentInstitutionId = sessionManager.currentInstitutionIdFlow.firstOrNull() 
            ?: return CurrentUserDataResult.NoInstitutionSelected
        
        return try {
            val perfiles = usuarioInstitucionRepository.getPerfilInstitucionalByUsuarioId(usuarioId)
            val currentProfile = perfiles.find { it.usuarioInstitucionId == currentInstitutionId }
                ?: return CurrentUserDataResult.InstitutionNotFound
            
            CurrentUserDataResult.Success(
                CurrentUserData(
                    nombreUsuario = currentProfile.institucionNombre,
                    nombreInstitucion = currentProfile.institucionNombre,
                    usuarioInstitucionId = currentInstitutionId
                )
            )
        } catch (e: Exception) {
            CurrentUserDataResult.Error(e.message ?: "Error desconocido")
        }
    }
}

data class CurrentUserData(
    val nombreUsuario: String,
    val nombreInstitucion: String,
    val usuarioInstitucionId: Int
)

sealed class CurrentUserDataResult {
    data class Success(val userData: CurrentUserData) : CurrentUserDataResult()
    object NotAuthenticated : CurrentUserDataResult()
    object InvalidToken : CurrentUserDataResult()
    object NoInstitutionSelected : CurrentUserDataResult()
    object InstitutionNotFound : CurrentUserDataResult()
    data class Error(val message: String) : CurrentUserDataResult()
}