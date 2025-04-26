package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.PacienteRepository
import com.nutrizulia.domain.model.Paciente
import javax.inject.Inject

class UpdatePaciente @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {

    suspend operator fun invoke(paciente: Paciente): Int {
        return pacienteRepository.updatePaciente(paciente)
    }

}