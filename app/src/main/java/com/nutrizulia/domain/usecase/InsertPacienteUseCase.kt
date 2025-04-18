package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.PacienteRepository
import com.nutrizulia.domain.model.Paciente
import javax.inject.Inject

class InsertPacienteUseCase @Inject constructor(private val repository: PacienteRepository) {
    suspend operator fun invoke(paciente: Paciente): Long {
        return repository.insertPaciente(paciente)
    }
}