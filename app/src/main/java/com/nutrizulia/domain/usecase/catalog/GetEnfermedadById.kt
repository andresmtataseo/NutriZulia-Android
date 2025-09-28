package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.EnfermedadRepository
import com.nutrizulia.domain.model.catalog.Enfermedad
import javax.inject.Inject

class GetEnfermedadById @Inject constructor(
    private val repository: EnfermedadRepository
) {
    suspend operator fun invoke(id: Int): Enfermedad? {
        return repository.findById(id)
    }
}