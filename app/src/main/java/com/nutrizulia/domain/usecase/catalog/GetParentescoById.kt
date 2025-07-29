package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParentescoRepository
import com.nutrizulia.domain.model.catalog.Parentesco
import javax.inject.Inject

class GetParentescoById @Inject constructor(
    private val repository: ParentescoRepository
) {
    suspend operator fun invoke(id: Int) : Parentesco? {
        return repository.findById(id)
    }
}