package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.RiesgoBiologicoDao
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class RiesgoBiologicoRepository @Inject constructor(
    private val dao: RiesgoBiologicoDao
) {
    suspend fun findAllByGeneroAndMeses(genero: String, edadMeses: Int): List<RiesgoBiologico> {
        return dao.findAllByGeneroAndMeses(genero, edadMeses).map { it.toDomain() }
    }
}