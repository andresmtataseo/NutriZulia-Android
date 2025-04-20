package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.CitaRepository
import com.nutrizulia.domain.model.Cita
import javax.inject.Inject

class InsertCitaUseCase @Inject constructor(private val repository: CitaRepository) {
    suspend operator fun invoke(cita: Cita): Long {
        return repository.insertCita(cita)
    }
}