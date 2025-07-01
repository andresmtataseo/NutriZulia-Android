package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.EspecialidadRepository
import javax.inject.Inject

class GetEspecialidadById @Inject constructor(
    private val repository: EspecialidadRepository
) {
    suspend operator fun invoke(id: Int) = repository.findById(id)
}