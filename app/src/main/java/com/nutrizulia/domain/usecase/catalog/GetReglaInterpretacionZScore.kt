package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ReglaInterpretacionZScoreRepository
import javax.inject.Inject

class GetReglaInterpretacionZScore @Inject constructor(
    private val repository: ReglaInterpretacionZScoreRepository
) {
    suspend operator fun invoke(tipoIndicadorId: Int, zScore: Double) = repository.findInterpretacion(tipoIndicadorId, zScore)
}