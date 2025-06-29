package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.NacionalidadRepository
import javax.inject.Inject

class GetNacionalidadById @Inject constructor(
    private val repository: NacionalidadRepository
) {
    suspend operator fun invoke(id: Int) = repository.findNacionalidadById(id)
}