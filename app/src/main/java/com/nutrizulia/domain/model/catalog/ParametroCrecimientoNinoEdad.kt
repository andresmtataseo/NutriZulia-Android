package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.ParametroCrecimientoNinoEdadEntity

data class ParametroCrecimientoNinoEdad(
    val id: Int,
    val tipoIndicadorId: Int,
    val grupoEtarioId: Int,
    val genero: String,
    val edadMes: Int,
    val lambda: Double,
    val mu: Double,
    val sigma: Double
)

fun ParametroCrecimientoNinoEdadEntity.toDomain() = ParametroCrecimientoNinoEdad(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    grupoEtarioId = grupoEtarioId,
    genero = genero,
    edadMes = edadMes,
    lambda = lambda,
    mu = mu,
    sigma = sigma
)