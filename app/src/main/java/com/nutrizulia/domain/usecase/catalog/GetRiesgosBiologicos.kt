package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.RiesgoBiologicoRepository
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
import javax.inject.Inject

class GetRiesgosBiologicos @Inject constructor(
    private val repository: RiesgoBiologicoRepository
) {
    suspend operator fun invoke(genero: String): List<RiesgoBiologico> {
        return repository.findAllByGenero(genero)
    }
}