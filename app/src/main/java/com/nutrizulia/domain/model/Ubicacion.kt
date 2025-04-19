package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.UbicacionEntity

data class Ubicacion(
    val id: Int,
    var codEntidad: String,
    var entidad: String,
    var codMunicipio: String,
    var municipio: String,
    var codParroquia: String,
    var parroquia: String
)

fun UbicacionEntity.toDomain() = Ubicacion(id, codEntidad, entidad, codMunicipio, municipio, codParroquia, parroquia)
