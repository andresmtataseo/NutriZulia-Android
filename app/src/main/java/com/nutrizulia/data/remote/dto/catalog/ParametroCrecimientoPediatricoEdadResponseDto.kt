package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoPediatricoEdadEntity

data class ParametroCrecimientoPediatricoEdadResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tipo_indicador_id") val tipoIndicadorId: Int,
    @SerializedName("grupo_etario_id") val grupoEtarioId: Int,
    @SerializedName("genero") val genero: String,
    @SerializedName("edad_dia") val edadDia: Int,
    @SerializedName("lambda") val lambda: Double,
    @SerializedName("mu") val mu: Double,
    @SerializedName("sigma") val sigma: Double
)

fun ParametroCrecimientoPediatricoEdadResponseDto.toEntity() = ParametroCrecimientoPediatricoEdadEntity(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    grupoEtarioId = grupoEtarioId,
    genero = genero,
    edadDia = edadDia,
    lambda = lambda,
    mu = mu,
    sigma = sigma
)