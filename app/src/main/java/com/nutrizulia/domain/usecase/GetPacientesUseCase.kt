package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.PacienteRepository
import com.nutrizulia.domain.model.Paciente
import javax.inject.Inject

class GetPacientesUseCase @Inject constructor(private val repository: PacienteRepository) {

    suspend operator fun invoke() : List<Paciente> {
        return repository.getPacientes()
    }

}