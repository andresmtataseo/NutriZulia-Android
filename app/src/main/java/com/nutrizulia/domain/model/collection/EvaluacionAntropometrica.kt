package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.EvaluacionAntropometricaEntity
import com.nutrizulia.data.local.enum.TipoValorCalculado
import java.time.LocalDate
import java.time.LocalDateTime

data class EvaluacionAntropometrica(
    val id: String,
    val consultaId: String,
    val detalleAntropometricoId: String,
    val tipoIndicadorId: Int,
    val valorCalculado: Double,
    val tipoValorCalculado: TipoValorCalculado,
    val diagnosticoAntropometrico: String,
    val fechaEvaluacion: LocalDate,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean
)

fun EvaluacionAntropometricaEntity.toDomain() = EvaluacionAntropometrica(
    id = id,
    consultaId = consultaId,
    detalleAntropometricoId = detalleAntropometricoId,
    tipoIndicadorId = tipoIndicadorId,
    valorCalculado = valorCalculado,
    tipoValorCalculado = tipoValorCalculado,
    diagnosticoAntropometrico = diagnosticoAntropometrico,
    fechaEvaluacion = fechaEvaluacion,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)