package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.RiesgoBiologicoDao
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class RiesgoBiologicoRepository @Inject constructor(
    private val dao: RiesgoBiologicoDao
) {
    suspend fun findAllByGenero(genero: String): List<RiesgoBiologico> {
        return dao.findAllByGenero(genero).map { it.toDomain() }
    }
}