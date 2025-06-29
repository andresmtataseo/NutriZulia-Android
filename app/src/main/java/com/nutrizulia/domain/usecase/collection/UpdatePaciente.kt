package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.domain.model.collection.Paciente
import javax.inject.Inject

class UpdatePaciente @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {

    suspend operator fun invoke(paciente: Paciente): Int {
        return pacienteRepository.updatePaciente(paciente)
    }

}