package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.data.repository.user.UsuarioInstitucionRepository
import com.nutrizulia.util.JwtUtils
import com.nutrizulia.util.TokenManager
import javax.inject.Inject

class GetPerfilesInstitucionales @Inject constructor(
    private val repository: UsuarioInstitucionRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): GetPerfilesResult {
        val token: String = tokenManager.getToken() ?: return GetPerfilesResult.Failure.NotAuthenticated()
        val usuarioId: Int = JwtUtils.extractIdUsuario(token) ?: return GetPerfilesResult.Failure.InvalidToken()
        return try {
            val perfiles: List<PerfilInstitucional> = repository.getPerfilInstitucionalByUsuarioId(usuarioId)
            GetPerfilesResult.Success(perfiles)
        } catch (e: Exception) {
            GetPerfilesResult.Failure.InvalidToken("Error al leer perfiles: ${e.message}")
        }
    }
}
sealed class GetPerfilesResult {
    data class Success(val perfiles: List<PerfilInstitucional>) : GetPerfilesResult()
    sealed class Failure(val message: String) : GetPerfilesResult() {
        class NotAuthenticated(message: String = "No se encontró sesión de usuario.") : Failure(message)
        class InvalidToken(message: String = "El token de sesión es inválido.") : Failure(message)
    }
}