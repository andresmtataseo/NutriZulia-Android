package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.TipoIndicadorDao
import com.nutrizulia.domain.model.catalog.TipoIndicador
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class TipoIndicadorRepository @Inject constructor(
    private val tipoIndicadorDao: TipoIndicadorDao
) {
    suspend fun findAll(): List<TipoIndicador> {
        return tipoIndicadorDao.findAll().map { it.toDomain() }
    }
}