package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.MunicipioDao
import com.nutrizulia.domain.model.catalog.Municipio
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class MunicipioRepository @Inject constructor(
    private val dao: MunicipioDao
) {

    suspend fun findAllByEstadoId(idEstado: Int): List<Municipio> {
        return dao.findAllByEstadoId(idEstado).map { it.toDomain() }
    }

    suspend fun findMunicipioById(id: Int): Municipio? {
        return dao.findMunicipioById(id)?.toDomain()
    }
}