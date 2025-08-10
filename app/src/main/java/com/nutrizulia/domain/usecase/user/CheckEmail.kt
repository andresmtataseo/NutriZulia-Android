package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.repository.user.UsuarioRepository
import javax.inject.Inject

class CheckEmail @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(email: String): Boolean {
        return repository.checkEmail(email)
    }
}