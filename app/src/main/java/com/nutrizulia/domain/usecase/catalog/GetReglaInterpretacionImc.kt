package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ReglaInterpretacionImcRepository
import javax.inject.Inject

class GetReglaInterpretacionImc @Inject constructor(
    private val repository: ReglaInterpretacionImcRepository
){
    suspend operator fun invoke(tipoIndicadorId: Int, imc: Double) = repository.findInterpretacionByTipoIndicadorIdAndImc(tipoIndicadorId, imc)
}