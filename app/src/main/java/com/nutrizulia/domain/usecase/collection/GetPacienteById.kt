package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepository
import javax.inject.Inject

class GetPacienteById @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(idPaciente: String) = pacienteRepository.findById(idPaciente)
}