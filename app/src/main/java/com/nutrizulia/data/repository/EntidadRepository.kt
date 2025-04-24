package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.EntidadDao
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.data.remote.service.EntidadService
import com.nutrizulia.domain.model.Entidad
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

class EntidadRepository @Inject constructor(
    private val dao: EntidadDao,
    private val api: EntidadService
){

    suspend fun getEntidades(): List<Entidad> {
        return dao.getEntidades().map { it.toDomain() }
    }

    suspend fun insertEntidades(entidades: List<Entidad>): List<Long> {
        return dao.insertEntidades(entidades.map { it.toEntity() })
    }

    suspend fun getEntidadesFromApi(token: String): List<Entidad> {
        return api.getEntidades(token).map { it.toDomain() }
    }
}