package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.ParroquiaEntity

data class Parroquia(
    val id: Int,
    val municipioId: Int,
    val nombre: String
)

fun ParroquiaEntity.toDomain() = Parroquia(
    id = id,
    municipioId = municipioId,
    nombre = nombre
)