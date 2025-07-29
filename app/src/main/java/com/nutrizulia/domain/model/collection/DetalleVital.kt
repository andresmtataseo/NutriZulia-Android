package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.DetalleVitalEntity
import java.time.LocalDateTime

data class DetalleVital(
    val id: String,
    val consultaId: String,
    val tensionArterialSistolica: Int?,
    val tensionArterialDiastolica: Int?,
    val frecuenciaCardiaca: Int?,
    val frecuenciaRespiratoria: Int?,
    val temperatura: Double?,
    val saturacionOxigeno: Int?,
    val pulso: Int?,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean
)

fun DetalleVitalEntity.toDomain() = DetalleVital(
    id = id,
    consultaId = consultaId,
    tensionArterialSistolica = tensionArterialSistolica,
    tensionArterialDiastolica = tensionArterialDiastolica,
    frecuenciaCardiaca = frecuenciaCardiaca,
    frecuenciaRespiratoria = frecuenciaRespiratoria,
    temperatura = temperatura,
    saturacionOxigeno = saturacionOxigeno,
    pulso = pulso,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)