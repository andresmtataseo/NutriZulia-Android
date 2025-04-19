package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.RepresentanteEntity

data class Representante(
    val id: Int,
    var parentesco: String,
    var pacienteId: Int,
    var representanteId: Int
)

fun RepresentanteEntity.toDomain() = Representante(id, parentesco, pacienteId, representanteId)
