package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.ParroquiaEntity
import com.nutrizulia.data.model.ParroquiaModel

data class Parroquia(
    val codEntidad: String,
    val codMunicipio: String,
    val codParroquia: String,
    val parroquia: String
)

fun ParroquiaEntity.toDomain() = Parroquia(
    codEntidad = codEntidad,
    codMunicipio = codMunicipio,
    codParroquia = codParroquia,
    parroquia = parroquia
)

fun ParroquiaModel.toDomain(codEntidadIne: String, codMunicipioIne: String) = Parroquia(
    codEntidad = codEntidadIne,
    codMunicipio = codMunicipioIne,
    codParroquia = codParroquiaIne,
    parroquia = parroquiaIne
)