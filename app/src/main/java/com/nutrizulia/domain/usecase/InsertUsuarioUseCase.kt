package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.UsuarioRepository
import com.nutrizulia.domain.model.Usuario
import javax.inject.Inject

class InsertUsuarioUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(usuario: Usuario): Long {
        return repository.insertUsuario(usuario)
    }
}