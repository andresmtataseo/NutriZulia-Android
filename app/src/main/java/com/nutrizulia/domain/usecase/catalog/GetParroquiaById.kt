package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParroquiaRepository
import javax.inject.Inject

class GetParroquiaById @Inject constructor(
    private val repository: ParroquiaRepository
) {
    suspend operator fun invoke(id: Int) = repository.findParroquiaById(id)
}