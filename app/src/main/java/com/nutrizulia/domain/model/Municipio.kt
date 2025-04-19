package com.nutrizulia.domain.model

import com.nutrizulia.data.local.dto.MunicipioDto

data class Municipio(
    val codMunicipio: String,
    val municipio: String
)

fun MunicipioDto.toDomain() = Municipio(
    codMunicipio = codMunicipio,
    municipio = municipio
)