package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParentescoRepository
import com.nutrizulia.domain.model.catalog.Parentesco
import javax.inject.Inject

class GetParentescos @Inject constructor(
    private val repository: ParentescoRepository
) {
    suspend operator fun invoke(): List<Parentesco> {
        return repository.findAll()
    }
}