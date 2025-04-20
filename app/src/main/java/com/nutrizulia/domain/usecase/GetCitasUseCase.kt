package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.CitaRepository
import com.nutrizulia.domain.model.Cita
import javax.inject.Inject

class GetCitasUseCase @Inject constructor(
    private val repository: CitaRepository
) {
    suspend operator fun invoke(): List<Cita> {
        return repository.getAllCitas()
    }
}