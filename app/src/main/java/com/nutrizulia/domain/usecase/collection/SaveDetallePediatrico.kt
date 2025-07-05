package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetallePediatricoRepository
import com.nutrizulia.domain.model.collection.DetallePediatrico
import javax.inject.Inject

class SaveDetallePediatrico @Inject constructor(
    private val repopsitory: DetallePediatricoRepository
) {
    suspend operator fun invoke(detallePediatrico: DetallePediatrico) {
        repopsitory.upsert(detallePediatrico)
    }
}