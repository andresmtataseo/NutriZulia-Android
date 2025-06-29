package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.EstadoRepository
import javax.inject.Inject

class GetEstadoById @Inject constructor(
    private val repository: EstadoRepository
) {
    suspend operator fun invoke(id: Int) = repository.findEstadoById(id)
}