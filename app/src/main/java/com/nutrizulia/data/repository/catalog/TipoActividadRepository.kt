package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.TipoActividadDao
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class TipoActividadRepository @Inject constructor(
    private val dao: TipoActividadDao
) {
    suspend fun findAll(): List<TipoActividad> {
        return dao.findAll().map { it.toDomain() }
    }
}