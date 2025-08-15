package com.nutrizulia.domain.model.dashboard

data class ResumenMensual(
    val totalConsultas: Int,
    val totalHombres: Int,
    val totalMujeres: Int,
    val totalNinos: Int,
    val totalNinas: Int
)