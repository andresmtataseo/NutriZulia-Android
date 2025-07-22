package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.repository.user.UsuarioInstitucionRepository
import com.nutrizulia.util.JwtUtils
import com.nutrizulia.util.TokenManager
import javax.inject.Inject

class SyncUsuarioInstituciones @Inject constructor(
    private val repository: UsuarioInstitucionRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): SyncResult {
        // 1. Obtener el token de forma segura.
        val token: String = tokenManager.getToken() ?: return SyncResult.Failure.NotAuthenticated()

        // 2. Extraer el ID del usuario del token.
        val usuarioId: Int = JwtUtils.extractIdUsuario(token) ?: return SyncResult.Failure.InvalidToken()

        // 3. Llamar al repositorio y manejar su resultado.
        return try {
            repository.syncUsuarioInstitucionByUsuarioId(usuarioId)
            SyncResult.Success
        } catch (e: Exception) {
            // Se captura cualquier excepción del repositorio y se convierte en un error controlado.
            SyncResult.Failure.ApiError("Error de red: ${e.message}")
        }
    }
}

// Este sealed class puede ser compartido por varios casos de uso de usuario.
sealed class SyncResult {
    object Success : SyncResult()
    sealed class Failure(val message: String) : SyncResult() {
        class NotAuthenticated(message: String = "No se encontró sesión de usuario.") : Failure(message)
        class InvalidToken(message: String = "El token de sesión es inválido.") : Failure(message)
        class ApiError(message: String) : Failure(message)
    }
}