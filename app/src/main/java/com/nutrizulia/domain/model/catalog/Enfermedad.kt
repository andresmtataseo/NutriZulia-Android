package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.EnfermedadEntity

data class Enfermedad(
    val id: Int,
    val codigoInternacional: String,
    val nombre: String,
    val genero: String
)

fun EnfermedadEntity.toDomain() = Enfermedad(
    id = id,
    codigoInternacional = codigoInternacional,
    nombre = nombre,
    genero = genero
)