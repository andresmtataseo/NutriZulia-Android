package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.TipoActividadRepository
import javax.inject.Inject

class GetTipoActividadById @Inject constructor(
    private val repository: TipoActividadRepository
) {
    suspend operator fun invoke(id: Int) = repository.findById(id)
}