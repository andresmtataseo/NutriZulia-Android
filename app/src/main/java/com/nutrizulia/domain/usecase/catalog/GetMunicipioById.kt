package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.MunicipioRepository
import javax.inject.Inject

class GetMunicipioById @Inject constructor(
    private val repository: MunicipioRepository
) {
    suspend operator fun invoke(id: Int) = repository.findMunicipioById(id)
}