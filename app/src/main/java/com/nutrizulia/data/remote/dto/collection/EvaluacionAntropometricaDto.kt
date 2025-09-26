package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.EvaluacionAntropometricaEntity
import com.nutrizulia.data.local.enum.TipoValorCalculado
import java.time.LocalDate
import java.time.LocalDateTime

data class EvaluacionAntropometricaDto(
    @SerializedName("id") val id: String,
    @SerializedName("consulta_id") val consultaId: String,
    @SerializedName("detalle_antropometrico_id") val detalleAntropometricoId: String,
    @SerializedName("tipo_indicador_id") val tipoIndicadorId: Int,
    @SerializedName("valor_calculado") val valorCalculado: Double,
    @SerializedName("tipo_valor_calculado") val tipoValorCalculado: TipoValorCalculado,
    @SerializedName("diagnostico_antropometrico") val diagnosticoAntropometrico: String,
    @SerializedName("fecha_evaluacion") val fechaEvaluacion: LocalDate,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean,
)

fun EvaluacionAntropometricaDto.toEntity() = EvaluacionAntropometricaEntity(
    id = id,
    consultaId = consultaId,
    detalleAntropometricoId = detalleAntropometricoId,
    tipoIndicadorId = tipoIndicadorId,
    valorCalculado = valorCalculado,
    tipoValorCalculado = tipoValorCalculado,
    diagnosticoAntropometrico = diagnosticoAntropometrico,
    fechaEvaluacion = fechaEvaluacion,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = true
)