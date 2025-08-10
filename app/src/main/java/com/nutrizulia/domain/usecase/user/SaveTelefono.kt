package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.repository.user.UsuarioRepository
import javax.inject.Inject

class SaveTelefono @Inject constructor(
    private val repository: UsuarioRepository
) {

    suspend operator fun invoke(idUsuario: Int, telefono: String): Result<ApiResponseDto<Void>> {
        return repository.savePhone(idUsuario, telefono)
    }

}