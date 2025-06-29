package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.ParroquiaDao
import com.nutrizulia.domain.model.catalog.Parroquia
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class ParroquiaRepository @Inject constructor(
    private val dao: ParroquiaDao
) {
    suspend fun findAllByMunicipioId(idMunicipio: Int): List<Parroquia> {
        return dao.findAllByMunicipioId(idMunicipio).map { it.toDomain() }
    }

    suspend fun findParroquiaById(id: Int): Parroquia? {
        return dao.findParroquiaById(id)?.toDomain()
    }

}