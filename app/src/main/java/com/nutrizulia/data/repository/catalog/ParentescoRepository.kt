package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.ParentescoDao
import com.nutrizulia.domain.model.catalog.Parentesco
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class ParentescoRepository @Inject constructor(
    private val parentescoDao: ParentescoDao
) {
    suspend fun findAll(): List<Parentesco> {
        return parentescoDao.findAll().map { it.toDomain() }
    }
    suspend fun findById(id: Int): Parentesco? {
        return parentescoDao.findById(id)?.toDomain()
    }
}