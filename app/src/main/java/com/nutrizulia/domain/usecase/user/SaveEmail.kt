package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.repository.user.UsuarioRepository
import javax.inject.Inject

class SaveEmail @Inject constructor(
    private val repository: UsuarioRepository
) {

    suspend operator fun invoke(idUsuario: Int, correo: String): Result<ApiResponseDto<Void>> {
        return repository.saveEmail(idUsuario, correo)
    }

}