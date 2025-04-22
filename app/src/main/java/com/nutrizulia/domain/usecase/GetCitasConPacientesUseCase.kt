package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.CitaRepository
import com.nutrizulia.domain.model.Cita
import com.nutrizulia.domain.model.CitaConPaciente
import javax.inject.Inject

class GetCitasConPacientesUseCase @Inject constructor(
    private val repository: CitaRepository
) {
    suspend operator fun invoke(): List<CitaConPaciente> {
        return repository.getAllCitasConPacientes()
    }
}