package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.ReglaInterpretacionImcDao
import javax.inject.Inject

class ReglaInterpretacionImcRepository @Inject constructor(
    private val dao: ReglaInterpretacionImcDao
) {

    suspend fun findInterpretacionByTipoIndicadorIdAndImc(tipoIndicadorId: Int, imc: Double): String? {
        return dao.findInterpretacionByTipoIndicadorIdAndImc(tipoIndicadorId, imc)
    }

}