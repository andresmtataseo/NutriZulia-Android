package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleMetabolicoRepository
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import javax.inject.Inject

class GetDetalleMetabolicoByConsultaId @Inject constructor(
    private val repository: DetalleMetabolicoRepository
) {
    suspend operator fun invoke(consultaId: String): DetalleMetabolico? {
        return repository.findByConsultaId(consultaId)
    }
}