package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.MunicipioEntity
import com.nutrizulia.data.model.MunicipioModel

data class Municipio(
    val codEntidad: String,
    val codMunicipio: String,
    val municipio: String
)

fun MunicipioEntity.toDomain() = Municipio(
    codEntidad = codEntidad,
    codMunicipio = codMunicipio,
    municipio = municipio
)

fun MunicipioModel.toDomain(codEntidadIne: String) = Municipio(
    codEntidad = codEntidadIne,
    codMunicipio = codMunicipioIne,
    municipio = municipioIne
)