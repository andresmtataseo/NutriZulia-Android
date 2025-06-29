package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.EstadoDao
import com.nutrizulia.domain.model.catalog.Estado
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class EstadoRepository @Inject constructor(
    private val dao: EstadoDao
) {
    suspend fun findAll() : List<Estado> {
        return dao.findAll().map { it.toDomain() }
    }

    suspend fun findEstadoById(id: Int) : Estado? {
        return dao.findEstadoById(id)?.toDomain()
    }

}