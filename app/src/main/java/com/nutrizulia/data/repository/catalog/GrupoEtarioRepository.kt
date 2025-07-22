package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.GrupoEtarioDao
import com.nutrizulia.domain.model.catalog.GrupoEtario
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class GrupoEtarioRepository @Inject constructor(
    private val dao: GrupoEtarioDao
) {
    suspend fun findAll(): List<GrupoEtario> {
        return dao.findAll().map { it.toDomain() }
    }

    suspend fun findByEdad(edadMes: Int): GrupoEtario? {
        return dao.findByEdad(edadMes)?.toDomain()
    }
}