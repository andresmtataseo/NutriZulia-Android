package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleMetabolicoRepository
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import javax.inject.Inject

class GetLatestDetalleMetabolicoByPacienteId @Inject constructor(
    private val detalleMetabolicoRepository: DetalleMetabolicoRepository
) {
    suspend operator fun invoke(pacienteId: String): DetalleMetabolico? {
        return detalleMetabolicoRepository.findLatestByPacienteId(pacienteId)
    }
}