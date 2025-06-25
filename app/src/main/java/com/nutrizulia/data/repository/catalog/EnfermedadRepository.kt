package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.EnfermedadDao
import com.nutrizulia.data.remote.api.catalog.CatalogService
import com.nutrizulia.domain.model.catalog.Enfermedad
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class EnfermedadRepository @Inject constructor(
    private val dao: EnfermedadDao,
    private val api: CatalogService
) {

    suspend fun getEnfermedadesFromDB(genero: String, nombre: String): List<Enfermedad> {
        return dao.findAllByGeneroAndNombreLike(genero, nombre).map { it.toDomain() }
    }

}