package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.repository.user.UsuarioInstitucionRepository
import javax.inject.Inject

class GetPerfilesInstitucionales @Inject constructor(
    private val usuarioInstitucionRepository: UsuarioInstitucionRepository
) {
    suspend operator fun invoke(usuarioId: Int) = usuarioInstitucionRepository.getPerfilInstitucionalByUsuarioId(usuarioId)
}