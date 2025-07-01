package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.EspecialidadDao
import com.nutrizulia.domain.model.catalog.Especialidad
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class EspecialidadRepository @Inject constructor(
    private val dao: EspecialidadDao
) {
    suspend fun findAll(): List<Especialidad> {
        return dao.findAll().map { it.toDomain() }
    }
    suspend fun findById(id: Int): Especialidad? {
        return dao.findById(id)?.toDomain()
    }
}