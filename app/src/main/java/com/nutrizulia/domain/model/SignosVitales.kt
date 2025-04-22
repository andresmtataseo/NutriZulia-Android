package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.SignosVitalesEntity

data class SignosVitales(
    var consultaId: Int,
    val peso: Double,
    val altura: Double,
    val temperatura: Double,
    val glicemia: Int,
    val pulso: Int,
    val tensionArterial: String,
    val frecuenciaCardiaca: Int,
    val frecuenciaRespiratoria: Int,
    val saturacionOxigeno: Int,
    val perimetroCefalico: Double,
    val circunferenciaBraquial: Double,
    val circunferenciaCintura: Double,
    val isEmbarazo: Boolean,
    val fechaUltimaMenstruacion: String,
    val semanasGestacion: Int,
    val tipoLactancia: String,
    val isTetero: Boolean,
    val relacionPesoAltura: Double,
    val relacionAlturaEdad: Double,
    val relacionPesoEdad: Double,
)

fun SignosVitalesEntity.toDomain() = SignosVitales(consultaId, peso, altura, temperatura, glicemia, pulso, tensionArterial, frecuenciaCardiaca, frecuenciaRespiratoria, saturacionOxigeno, perimetroCefalico, circunferenciaBraquial, circunferenciaCintura, isEmbarazo, fechaUltimaMenstruacion, semanasGestacion, tipoLactancia, isTetero, relacionPesoAltura, relacionAlturaEdad, relacionPesoEdad)
