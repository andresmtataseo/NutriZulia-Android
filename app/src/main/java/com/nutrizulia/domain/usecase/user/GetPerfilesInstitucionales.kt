package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.data.repository.user.UsuarioInstitucionRepository
import com.nutrizulia.util.JwtUtils
import com.nutrizulia.util.TokenManager
import javax.inject.Inject

class GetPerfilesInstitucionales @Inject constructor(
    private val repository: UsuarioInstitucionRepository,
    // ✅ 1. Inyectar TokenManager para que sea autónomo
    private val tokenManager: TokenManager
) {
    // ✅ 2. El método invoke ya no recibe parámetros
    suspend operator fun invoke(): GetPerfilesResult {
        // 3. Obtener el token de forma segura
        val token: String = tokenManager.getToken() ?: return GetPerfilesResult.Failure.NotAuthenticated()

        // 4. Extraer el ID del usuario del token
        val usuarioId: Int = JwtUtils.extractIdUsuario(token) ?: return GetPerfilesResult.Failure.InvalidToken()

        // 5. Llamar al repositorio con el ID y devolver el resultado encapsulado
        return try {
            val perfiles: List<PerfilInstitucional> = repository.getPerfilInstitucionalByUsuarioId(usuarioId)
            GetPerfilesResult.Success(perfiles)
        } catch (e: Exception) {
            // Aunque es raro en una lectura de BD, es buena práctica capturar excepciones
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