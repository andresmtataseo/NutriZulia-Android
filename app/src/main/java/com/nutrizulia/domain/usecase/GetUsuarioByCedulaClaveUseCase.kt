package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.UsuarioRepository
import com.nutrizulia.domain.model.Usuario
import javax.inject.Inject

class GetUsuarioByCedulaClaveUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(cedula: String, clave: String): Usuario? {
        return repository.getUsuarioByCedulaAndClave(cedula, clave)
    }
}