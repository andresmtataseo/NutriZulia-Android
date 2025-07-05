package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleVitalRepository
import com.nutrizulia.domain.model.collection.DetalleVital
import javax.inject.Inject

class SaveDetalleVital @Inject constructor(
    private val repository: DetalleVitalRepository
){
    suspend operator fun invoke(detalleVital: DetalleVital) {
        repository.upsert(detalleVital)
    }
}