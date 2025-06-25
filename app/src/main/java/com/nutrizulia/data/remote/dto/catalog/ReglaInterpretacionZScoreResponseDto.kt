package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionZScoreEntity

data class ReglaInterpretacionZScoreResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tipo_indicador_id") val tipoIndicadorId: Int,
    @SerializedName("z_score_minimo") val zScoreMinimo: Double,
    @SerializedName("z_score_maximo") val zScoreMaximo: Double,
    @SerializedName("diagnostico") val diagnostico: String
)

fun ReglaInterpretacionZScoreResponseDto.toEntity() = ReglaInterpretacionZScoreEntity(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    zScoreMinimo = zScoreMinimo,
    zScoreMaximo = zScoreMaximo,
    diagnostico = diagnostico
)