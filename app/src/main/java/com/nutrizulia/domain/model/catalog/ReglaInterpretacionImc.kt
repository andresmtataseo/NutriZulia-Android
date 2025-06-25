package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionImcEntity

data class ReglaInterpretacionImc(
    val id: Int,
    val tipoIndicadorId: Int,
    val imcMinimo: Double?,
    val imcMaximo: Double?,
    val diagnostico: String
)

fun ReglaInterpretacionImcEntity.toDomain() = ReglaInterpretacionImc(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    imcMinimo = imcMinimo,
    imcMaximo = imcMaximo,
    diagnostico = diagnostico
)