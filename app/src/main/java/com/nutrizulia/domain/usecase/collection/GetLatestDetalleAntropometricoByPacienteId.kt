package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleAntropometricoRepository
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import javax.inject.Inject

class GetLatestDetalleAntropometricoByPacienteId @Inject constructor(
    private val detalleAntropometricoRepository: DetalleAntropometricoRepository
) {
    suspend operator fun invoke(pacienteId: String): DetalleAntropometrico? {
        return detalleAntropometricoRepository.findLatestByPacienteId(pacienteId)
    }
}