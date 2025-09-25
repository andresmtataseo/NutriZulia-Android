package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleVitalRepository
import com.nutrizulia.domain.model.collection.DetalleVital
import javax.inject.Inject

class GetLatestDetalleVitalByPacienteId @Inject constructor(
    private val detalleVitalRepository: DetalleVitalRepository
) {
    suspend operator fun invoke(pacienteId: String): DetalleVital? {
        return detalleVitalRepository.findLatestByPacienteId(pacienteId)
    }
}