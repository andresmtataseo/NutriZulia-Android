package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.ComunidadDao
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.data.remote.service.ComunidadService
import com.nutrizulia.domain.model.Comunidad
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

class ComunidadRepository @Inject constructor(
    private val dao: ComunidadDao,
    private val api: ComunidadService
) {

    suspend fun getComunidad(codEntidad: String, codMunicipio: String, codParroquia: String, idComunidad: String): Comunidad {
        return dao.getComunidad(codEntidad, codMunicipio, codParroquia, idComunidad).toDomain()
    }

    suspend fun getComunidades(codEntidad: String, codMunicipio: String, codParroquia: String): List<Comunidad> {
        return dao.getComunidades(codEntidad, codMunicipio, codParroquia).map { it.toDomain() }
    }

    suspend fun insertComunidades(comunidades: List<Comunidad>): List<Long> {
        return dao.insertComunidades(comunidades.map { it.toEntity() })
    }

    suspend fun getComunidadesFromApi(token: String, codEntidad: String, codMunicipio: String, codParroquia: String): List<Comunidad> {
        return api.getComunidades(token, codEntidad, codMunicipio, codParroquia).map { it.toDomain(codEntidad, codMunicipio, codParroquia) }
    }

}