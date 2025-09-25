package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleObstetriciaRepository
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import javax.inject.Inject

class GetLatestDetalleObstetriciaByPacienteId @Inject constructor(
    private val detalleObstetriciaRepository: DetalleObstetriciaRepository
) {
    suspend operator fun invoke(pacienteId: String): DetalleObstetricia? {
        return detalleObstetriciaRepository.findLatestByPacienteId(pacienteId)
    }
}