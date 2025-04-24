package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.ComunidadEntity
import com.nutrizulia.data.model.ComunidadModel

data class Comunidad(
    val codEntidad: String,
    val codMunicipio: String,
    val codParroquia: String,
    val idComunidad: String,
    val nombreComunidad: String
)

fun ComunidadEntity.toDomain() = Comunidad(
    codEntidad = codEntidad,
    codMunicipio = codMunicipio,
    codParroquia = codParroquia,
    idComunidad = idComunidad,
    nombreComunidad = nombreComunidad
)

fun ComunidadModel.toDomain(codEntidadIne: String, codMunicipioIne: String, codParroquiaIne: String) = Comunidad(
    codEntidad = codEntidadIne,
    codMunicipio = codMunicipioIne,
    codParroquia = codParroquiaIne,
    idComunidad = idComunidadIne,
    nombreComunidad = nombreComunidadIne
)