package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.EtniaRepository
import javax.inject.Inject

class GetEtniaById @Inject constructor(
    private val repository: EtniaRepository
) {
    suspend operator fun invoke(id: Int) = repository.findEtniaById(id)
}