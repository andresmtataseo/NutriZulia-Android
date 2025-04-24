package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.ParroquiaDao
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.data.remote.service.ParroquiaService
import com.nutrizulia.domain.model.Parroquia
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

class ParroquiaRepository @Inject constructor(
    private val dao: ParroquiaDao,
    private val api: ParroquiaService
) {

    suspend fun getParroquias(codEntidad: String, codMunicipio: String): List<Parroquia> {
        return dao.getParroquias(codEntidad, codMunicipio).map { it.toDomain() }
    }

    suspend fun insertParroquias(parroquias: List<Parroquia>): List<Long> {
        return dao.insertParroquias(parroquias.map { it.toEntity() })
    }

    suspend fun getParroquiasFromApi(token: String, codEntidad: String, codMunicipio: String): List<Parroquia> {
        return api.getParroquias(token, codEntidad, codMunicipio).map { it.toDomain(codEntidad, codMunicipio) }
    }

}