package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.repository.user.UsuarioRepository
import com.nutrizulia.domain.model.user.Usuario
import javax.inject.Inject

class GetUserDetails @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(id: Int) : Usuario? {
        return repository.findById(id)
    }
}