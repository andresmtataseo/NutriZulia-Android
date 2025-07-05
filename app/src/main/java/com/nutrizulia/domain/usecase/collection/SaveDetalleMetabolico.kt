package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleMetabolicoRepository
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import javax.inject.Inject

class SaveDetalleMetabolico @Inject constructor(
    private val repository: DetalleMetabolicoRepository
) {
    suspend operator fun invoke(detalleMetabolico: DetalleMetabolico) {
        repository.upsert(detalleMetabolico)
    }
}