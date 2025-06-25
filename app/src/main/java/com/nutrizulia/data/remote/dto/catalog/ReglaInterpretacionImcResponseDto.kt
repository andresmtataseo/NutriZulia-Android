package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.ReglaInterpretacionImcEntity

data class ReglaInterpretacionImcResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tipo_indicador_id") val tipoIndicadorId: Int,
    @SerializedName("imc_minimo") val imcMinimo: Double,
    @SerializedName("imc_maximo") val imsMaximo: Double,
    @SerializedName("diagnostico") val diagnostico: String
)

fun ReglaInterpretacionImcResponseDto.toEntity() = ReglaInterpretacionImcEntity(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    imcMinimo = imcMinimo,
    imcMaximo = imsMaximo,
    diagnostico = diagnostico
)