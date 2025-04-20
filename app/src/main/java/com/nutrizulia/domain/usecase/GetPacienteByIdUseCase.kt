package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.PacienteRepository
import com.nutrizulia.domain.model.Paciente
import javax.inject.Inject

class GetPacienteByIdUseCase @Inject constructor(
    private val repository: PacienteRepository
){
    suspend operator fun invoke(idPaciente: Int): Paciente? {
        return repository.getPacienteById(idPaciente)
    }
}