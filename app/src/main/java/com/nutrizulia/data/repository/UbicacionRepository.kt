package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.UbicacionDao
import com.nutrizulia.data.local.entity.UbicacionEntity
import com.nutrizulia.data.remote.service.UbicacionService
import com.nutrizulia.domain.model.Entidad
import com.nutrizulia.domain.model.Municipio
import com.nutrizulia.domain.model.Parroquia
import com.nutrizulia.domain.model.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UbicacionRepository @Inject constructor(
    private val ubicacionDao: UbicacionDao,
    private val ubicacionService: UbicacionService
) {

    suspend fun descargarUbicaciones() = withContext(Dispatchers.IO) {

        val entidades = ubicacionService.getEntidades()
        val listaUbicaciones = mutableListOf<UbicacionEntity>()

        for (entidad in entidades) {
            val municipios = ubicacionService.getMunicipios(entidad.codEntidadIne)
            for (municipio in municipios) {
                val parroquias = ubicacionService.getParroquias(
                    codEntidad = entidad.codEntidadIne,
                    codMunicipio = municipio.codMunicipioIne
                )

                for (parroquia in parroquias) {
                    val ubicacion = UbicacionEntity(
                        codEntidad = entidad.codEntidadIne,
                        entidad = entidad.entidadIne,
                        codMunicipio = municipio.codMunicipioIne,
                        municipio = municipio.municipioIne,
                        codParroquia = parroquia.codParroquiaIne,
                        parroquia = parroquia.parroquiaIne
                    )
                    listaUbicaciones.add(ubicacion)
                }
            }
        }

        // Guardar en la base de datos
        listaUbicaciones.forEach { ubicacionDao.insertUbicacion(it) }
    }

    suspend fun getEntidades(): List<Entidad> {
       return ubicacionDao.getEntidades().map { it.toDomain() }
    }

    suspend fun getMunicipios(codEntidad: String): List<Municipio> {
        return ubicacionDao.getMunicipios(codEntidad).map{ it.toDomain() }
    }

    suspend fun getParroquias(codEntidad: String, codMunicipio: String): List<Parroquia> {
        return ubicacionDao.getParroquias(codEntidad, codMunicipio).map{ it.toDomain() }
    }
}
