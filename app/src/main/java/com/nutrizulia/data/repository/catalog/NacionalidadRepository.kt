package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.NacionalidadDao
import com.nutrizulia.domain.model.catalog.Nacionalidad
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class NacionalidadRepository @Inject constructor(
    private val dao: NacionalidadDao
) {

    suspend fun findAll(): List<Nacionalidad> {
        return dao.findAll().map { it.toDomain() }
    }

    suspend fun findNacionalidadById(id: Int): Nacionalidad? {
        return dao.findNacionalidadById(id)?.toDomain()
    }
}