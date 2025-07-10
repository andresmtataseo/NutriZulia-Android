package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetallePediatricoRepository
import com.nutrizulia.domain.model.collection.DetallePediatrico
import javax.inject.Inject

class GetDetallePediatricoByConsultaId @Inject constructor(
    private val repository: DetallePediatricoRepository
) {
    suspend operator fun invoke(consultaId: String): DetallePediatrico? {
        return repository.findByConsultaId(consultaId)
    }
}