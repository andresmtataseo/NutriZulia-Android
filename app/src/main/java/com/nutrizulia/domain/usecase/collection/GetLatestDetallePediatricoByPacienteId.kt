package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetallePediatricoRepository
import com.nutrizulia.domain.model.collection.DetallePediatrico
import javax.inject.Inject

class GetLatestDetallePediatricoByPacienteId @Inject constructor(
    private val detallePediatricoRepository: DetallePediatricoRepository
) {
    suspend operator fun invoke(pacienteId: String): DetallePediatrico? {
        return detallePediatricoRepository.findLatestByPacienteId(pacienteId)
    }
}