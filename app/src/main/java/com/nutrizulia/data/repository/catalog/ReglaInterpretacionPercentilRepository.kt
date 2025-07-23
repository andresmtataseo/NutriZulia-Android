package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.ReglaInterpretacionPercentilDao
import javax.inject.Inject

class ReglaInterpretacionPercentilRepository @Inject constructor(
    private val dao: ReglaInterpretacionPercentilDao
) {

    suspend fun findInterpretacionByTipoIndicadorIdAndPercentil(tipoIndicadorId: Int, percentil: Double): String? {
        return dao.findInterpretacionByTipoIndicadorIdAndPercentil(tipoIndicadorId, percentil)
    }

}