package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.DetalleAntropometricoEntity
import java.time.LocalDateTime

data class DetalleAntropometricoDto(
    @SerializedName("id") val id: String,
    @SerializedName("consulta_id") val consultaId: String,
    @SerializedName("peso") val peso: Double?,
    @SerializedName("altura") val altura: Double?,
    @SerializedName("talla") val talla: Double?,
    @SerializedName("circunferencia_braquial") val circunferenciaBraquial: Double?,
    @SerializedName("circunferencia_cadera") val circunferenciaCadera: Double?,
    @SerializedName("circunferencia_cintura") val circunferenciaCintura: Double?,
    @SerializedName("perimetro_cefalico") val perimetroCefalico: Double?,
    @SerializedName("pliegue_tricipital") val pliegueTricipital: Double?,
    @SerializedName("pliegue_subescapular") val pliegueSubescapular: Double?,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean,
)

fun DetalleAntropometricoDto.toEntity() = DetalleAntropometricoEntity(
    id = id,
    consultaId = consultaId,
    peso = peso,
    altura = altura,
    talla = talla,
    circunferenciaBraquial = circunferenciaBraquial,
    circunferenciaCadera = circunferenciaCadera,
    circunferenciaCintura = circunferenciaCintura,
    perimetroCefalico = perimetroCefalico,
    pliegueTricipital = pliegueTricipital,
    pliegueSubescapular = pliegueSubescapular,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = true
)