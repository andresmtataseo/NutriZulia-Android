package com.nutrizulia.data.repository

import androidx.room.Dao
import com.nutrizulia.data.local.dao.MunicipioDao
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.data.remote.service.MunicipioService
import com.nutrizulia.domain.model.Municipio
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

@Dao
class MunicipioRepository @Inject constructor(
    private val dao: MunicipioDao,
    private val api: MunicipioService
){

    suspend fun getMunicipios(codEntidad: String): List<Municipio> {
        return dao.getMunicipios(codEntidad).map { it.toDomain() }
    }

    suspend fun insertMunicipios(municipios: List<Municipio>): List<Long> {
        return dao.insertMunicipios(municipios.map { it.toEntity() })
    }

    suspend fun getMunicipiosFromApi(token: String, codEntidad: String): List<Municipio> {
        return api.getMunicipios(token, codEntidad).map { it.toDomain(codEntidad) }
    }

}