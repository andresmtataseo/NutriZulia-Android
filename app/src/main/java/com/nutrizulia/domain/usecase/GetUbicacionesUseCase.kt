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
        val lista = repository.getEntidades()
        if (lista.isEmpty()) {
            repository.descargarUbicaciones()
        }
        return lista
    }
    suspend fun getMunicipios(codEntidad: String): List<Municipio> {
        return repository.getMunicipios(codEntidad)
    }
    suspend fun getParroquias(codEntidad: String, codMunicipio: String): List<Parroquia> {
        return repository.getParroquias(codEntidad, codMunicipio)
    }
}