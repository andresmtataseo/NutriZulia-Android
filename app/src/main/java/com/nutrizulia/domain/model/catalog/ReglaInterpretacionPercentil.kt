package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionPercentilEntity

data class ReglaInterpretacionPercentil(
    val id: Int,
    val tipoIndicadorId: Int,
    val percentilMinimo: Double?,
    val percentilMaximo: Double?,
    val diagnostico: String
)

fun ReglaInterpretacionPercentilEntity.toDomain() = ReglaInterpretacionPercentil(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    percentilMinimo = percentilMinimo,
    percentilMaximo = percentilMaximo,
    diagnostico = diagnostico
)