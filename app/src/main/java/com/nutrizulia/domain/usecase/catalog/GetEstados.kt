package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.EstadoRepository
import com.nutrizulia.domain.model.catalog.Estado
import javax.inject.Inject

class GetEstados @Inject constructor(
    private val repository: EstadoRepository
) {
    suspend operator fun invoke(): List<Estado> {
        return repository.findAll()

    }
}