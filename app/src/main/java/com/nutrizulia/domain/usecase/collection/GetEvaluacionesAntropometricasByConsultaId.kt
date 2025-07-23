package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.EvaluacionAntropometricaRepository
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import javax.inject.Inject

class GetEvaluacionesAntropometricasByConsultaId @Inject constructor(
    private val repository: EvaluacionAntropometricaRepository
) {
    suspend operator fun invoke(idConsulta: String): List<EvaluacionAntropometrica> {
        return repository.findAllByConsultaId(idConsulta)
    }
}