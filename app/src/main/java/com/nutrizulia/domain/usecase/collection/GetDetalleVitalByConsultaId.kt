package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleVitalRepository
import com.nutrizulia.domain.model.collection.DetalleVital
import javax.inject.Inject

class GetDetalleVitalByConsultaId @Inject constructor(
    private val repository: DetalleVitalRepository
) {
    suspend operator fun invoke(consultaId: String) : DetalleVital? {
        return repository.findByConsultaId(consultaId)
    }
}