package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.repository.user.UsuarioRepository
import javax.inject.Inject

class CheckPhoneNumber @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(phoneNumber: String): Boolean {
        return repository.checkPhoneNumber(phoneNumber)
    }
}