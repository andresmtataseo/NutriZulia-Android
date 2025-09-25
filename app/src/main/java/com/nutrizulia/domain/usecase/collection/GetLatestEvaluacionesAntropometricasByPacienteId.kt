package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.EvaluacionAntropometricaRepository
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import com.nutrizulia.domain.model.catalog.TipoIndicador
import javax.inject.Inject

class GetLatestEvaluacionesAntropometricasByPacienteId @Inject constructor(
    private val evaluacionAntropometricaRepository: EvaluacionAntropometricaRepository
) {
    suspend operator fun invoke(pacienteId: String): Map<TipoIndicador, EvaluacionAntropometrica> {
        return evaluacionAntropometricaRepository.findLatestEvaluacionesByPacienteId(pacienteId)
    }
}