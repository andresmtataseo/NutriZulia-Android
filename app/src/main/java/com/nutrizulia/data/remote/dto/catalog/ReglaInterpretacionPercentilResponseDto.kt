package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionPercentilEntity

data class ReglaInterpretacionPercentilResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tipo_indicador_id") val tipoIndicadorId: Int,
    @SerializedName("percentil_minimo") val percentilMinimo: Double?,
    @SerializedName("percentil_maximo") val percentilMaximo: Double?,
    @SerializedName("descripcion") val diagnostico: String
)

fun ReglaInterpretacionPercentilResponseDto.toEntity() = ReglaInterpretacionPercentilEntity(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    percentilMinimo = percentilMinimo,
    percentilMaximo = percentilMaximo,
    diagnostico = diagnostico
)