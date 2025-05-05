package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.CitaRepository
import javax.inject.Inject

class UpdateEstadoCitaUseCase @Inject constructor(
    private val citaRepository: CitaRepository
) {
    suspend operator fun invoke(idCita: Int, nuevoEstado: String): Int {
        return citaRepository.updateEstadoCita(idCita, nuevoEstado)
    }

}