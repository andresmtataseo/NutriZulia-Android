package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.RepresentanteDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class RepresentanteRepository @Inject constructor(
    private val dao: RepresentanteDao
) {
    suspend fun findAll(idUsuarioInstitucion: Int): List<Representante> {
        return dao.findAll(idUsuarioInstitucion).map { it.toDomain() }
    }

    suspend fun findByFiltro(idUsuarioInstitucion: Int, filtro: String): List<Representante> {
        return dao.findAllByUsuarioInstitucionIdAndFilter(idUsuarioInstitucion, filtro).map { it.toDomain() }
    }

    suspend fun upsert(representante: Representante) {
        dao.upsert(representante.toEntity())
    }

    suspend fun findByCedula(usuarioInstitucionId: Int, cedula: String): Representante? {
        return dao.findByCedula(usuarioInstitucionId, cedula)?.toDomain()
    }

    suspend fun findById(id: Int): Representante? {
        return dao.findById(id)?.toDomain()
    }
}