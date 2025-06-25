package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoPediatricoEdadEntity

data class ParametroCrecimientoPediatricoEdad(
    val id: Int,
    val tipoIndicadorId: Int,
    val grupoEtarioId: Int,
    val genero: String,
    val edadDia: Int,
    val lambda: Double,
    val mu: Double,
    val sigma: Double
)

fun ParametroCrecimientoPediatricoEdadEntity.toDomain() = ParametroCrecimientoPediatricoEdad(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    grupoEtarioId = grupoEtarioId,
    genero = genero,
    edadDia = edadDia,
    lambda = lambda,
    mu = mu,
    sigma = sigma
)