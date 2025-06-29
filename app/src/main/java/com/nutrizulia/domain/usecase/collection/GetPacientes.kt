package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepository
import javax.inject.Inject

class GetPacientes @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int) = pacienteRepository.findAll(usuarioInstitucionId)

}