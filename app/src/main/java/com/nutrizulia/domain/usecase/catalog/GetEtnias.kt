package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.EtniaRepository
import com.nutrizulia.domain.model.catalog.Etnia
import javax.inject.Inject

class GetEtnias @Inject constructor(
    private val repository: EtniaRepository
) {
    suspend operator fun invoke(): List<Etnia> {
        return repository.findAll()
    }
}
