package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoPediatricoLongitudEntity

data class ParametroCrecimientoPediatricoLongitud(
    val id: Int,
    val tipoIndicadorId: Int,
    val grupoEtarioId: Int,
    val genero: String,
    val longitudCm: Double,
    val tipoMedicion: String,
    val lambda: Double,
    val mu: Double,
    val sigma: Double
)

fun ParametroCrecimientoPediatricoLongitudEntity.toDomain() = ParametroCrecimientoPediatricoLongitud(
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