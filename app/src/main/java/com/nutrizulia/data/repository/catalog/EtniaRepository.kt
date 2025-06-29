package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.EtniaDao
import com.nutrizulia.data.remote.api.catalog.CatalogService
import com.nutrizulia.domain.model.catalog.Etnia
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class EtniaRepository @Inject constructor(
    private val dao: EtniaDao
) {

    suspend fun findAll(): List<Etnia> {
        return dao.findAll().map { it.toDomain() }
    }

    suspend fun findEtniaById(id: Int): Etnia? {
        return dao.findEtniaById(id)?.toDomain()
    }
}
