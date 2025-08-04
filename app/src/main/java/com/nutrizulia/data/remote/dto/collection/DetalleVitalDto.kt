package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.DetalleVitalEntity
import java.time.LocalDateTime

data class DetalleVitalDto(
    @SerializedName("id") val id: String,
    @SerializedName("consulta_id") val consultaId: String,
    @SerializedName("tension_arterial_sistolica") val tensionArterialSistolica: Int?,
    @SerializedName("tension_arterial_diastolica") val tensionArterialDiastolica: Int?,
    @SerializedName("frecuencia_cardiaca") val frecuenciaCardiaca: Int?,
    @SerializedName("frecuencia_respiratoria") val frecuenciaRespiratoria: Int?,
    @SerializedName("temperatura") val temperatura: Double?,
    @SerializedName("saturacion_oxigeno") val saturacionOxigeno: Int?,
    @SerializedName("pulso") val pulso: Int?,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean,
)

fun DetalleVitalDto.toEntity() = DetalleVitalEntity(
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
    isDeleted = isDeleted,
    isSynced = false
)