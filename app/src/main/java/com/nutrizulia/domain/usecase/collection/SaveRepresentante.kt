package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.RepresentanteRepository
import com.nutrizulia.domain.model.collection.Representante
import javax.inject.Inject

class SaveRepresentante @Inject constructor(
    private val repository: RepresentanteRepository
) {
    suspend operator fun invoke(representante: Representante) {
        repository.upsert(representante)
    }
}