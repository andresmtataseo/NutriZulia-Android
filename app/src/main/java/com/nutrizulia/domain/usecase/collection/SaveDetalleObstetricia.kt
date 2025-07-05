package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleObstetriciaRepository
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import javax.inject.Inject

class SaveDetalleObstetricia @Inject constructor(
    private val repository: DetalleObstetriciaRepository
) {
    suspend operator fun invoke(it: DetalleObstetricia) {
        repository.upsert(it)
    }
}