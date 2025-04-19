package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.UbicacionRepository
import com.nutrizulia.domain.model.Entidad
import com.nutrizulia.domain.model.Municipio
import com.nutrizulia.domain.model.Parroquia
import javax.inject.Inject

class GetUbicacionesUseCase @Inject constructor(
    private val repository: UbicacionRepository
) {
    suspend fun getEntidades(): List<Entidad> {
        // devolver de la base de datos las entidades
        return repository.getEntidades()
        // sino hay, consultar la api
    }
    suspend fun getMunicipios(codEntidad: String): List<Municipio> {
        return repository.getMunicipios(codEntidad)
    }
    suspend fun getParroquias(codEntidad: String, codMunicipio: String): List<Parroquia> {
        return repository.getParroquias(codEntidad, codMunicipio)
    }
}