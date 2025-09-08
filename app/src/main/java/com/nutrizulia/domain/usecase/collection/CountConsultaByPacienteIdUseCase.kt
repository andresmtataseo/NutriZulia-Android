package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ConsultaRepository
import javax.inject.Inject

class CountConsultaByPacienteIdUseCase @Inject constructor(
    private val consultaRepository: ConsultaRepository
) {
    suspend operator fun invoke(pacienteId: String): Boolean {
        return consultaRepository.countConsultaByPacienteId(pacienteId)
    }
}