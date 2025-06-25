package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionZScoreEntity

data class ReglaInterpretacionZScore(
    val id: Int,
    val tipoIndicadorId: Int,
    val zScoreMinimo: Double?,
    val zScoreMaximo: Double?,
    val diagnostico: String
)

fun ReglaInterpretacionZScoreEntity.toDomain() = ReglaInterpretacionZScore(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    zScoreMinimo = zScoreMinimo,
    zScoreMaximo = zScoreMaximo,
    diagnostico = diagnostico
)