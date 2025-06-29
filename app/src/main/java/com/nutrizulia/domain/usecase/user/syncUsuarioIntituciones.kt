package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.repository.user.UsuarioInstitucionRepository
import javax.inject.Inject

class syncUsuarioIntituciones @Inject constructor(
    private val usuarioInstitucionRepository: UsuarioInstitucionRepository
) {
    suspend operator fun invoke(usuarioId: Int) = usuarioInstitucionRepository.syncUsuarioInstitucionByUsuarioId(usuarioId)

}