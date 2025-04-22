package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.CitaRepository
import com.nutrizulia.domain.model.CitaConPaciente
import javax.inject.Inject

class GetCitaConPacienteUseCase @Inject constructor(
    private val citaRepository: CitaRepository
) {
    suspend operator fun invoke(idCita: Int): CitaConPaciente {
        return citaRepository.getCitaConPaciente(idCita)
    }
}