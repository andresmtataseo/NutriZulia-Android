package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.ReglaInterpretacionZScoreDao
import javax.inject.Inject

class ReglaInterpretacionZScoreRepository @Inject constructor(
    private val dao: ReglaInterpretacionZScoreDao
) {

    suspend fun findInterpretacion(tipoIndicadorId: Int, zScore: Double): String? {
        return dao.findInterpretacion(tipoIndicadorId, zScore)
    }

}