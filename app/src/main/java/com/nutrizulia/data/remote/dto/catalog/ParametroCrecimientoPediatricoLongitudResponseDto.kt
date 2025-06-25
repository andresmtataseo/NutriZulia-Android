package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoPediatricoLongitudEntity

data class ParametroCrecimientoPediatricoLongitudResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tipo_indicador_id") val tipoIndicadorId: Int,
    @SerializedName("grupo_etario_id") val grupoEtarioId: Int,
    @SerializedName("genero") val genero: String,
    @SerializedName("longitud_cm") val longitudCm: Double,
    @SerializedName("tipo_medicion") val tipoMedicion: String,
    @SerializedName("lambda") val lambda: Double,
    @SerializedName("mu") val mu: Double,
    @SerializedName("sigma") val sigma: Double
)

fun ParametroCrecimientoPediatricoLongitudResponseDto.toEntity() = ParametroCrecimientoPediatricoLongitudEntity(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    grupoEtarioId = grupoEtarioId,
    genero = genero,
    longitudCm = longitudCm,
    tipoMedicion = tipoMedicion,
    lambda = lambda,
    mu = mu,
    sigma = sigma
)