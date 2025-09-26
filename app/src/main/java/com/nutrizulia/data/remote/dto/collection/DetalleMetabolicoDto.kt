package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.DetalleMetabolicoEntity
import java.time.LocalDateTime

data class DetalleMetabolicoDto(
    @SerializedName("id") val id: String,
    @SerializedName("consulta_id") val consultaId: String,
    @SerializedName("glicemia_basal") val glicemiaBasal: Int?,
    @SerializedName("glicemia_postprandial") val glicemiaPostprandial: Int?,
    @SerializedName("glicemia_aleatoria") val glicemiaAleatoria: Int?,
    @SerializedName("hemoglobina_glicosilada") val hemoglobinaGlicosilada: Double?,
    @SerializedName("trigliceridos") val trigliceridos: Int?,
    @SerializedName("colesterol_total") val colesterolTotal: Int?,
    @SerializedName("colesterol_hdl") val colesterolHdl: Int?,
    @SerializedName("colesterol_ldl") val colesterolLdl: Int?,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean,
)

fun DetalleMetabolicoDto.toEntity() = DetalleMetabolicoEntity(
    id = id,
    consultaId = consultaId,
    glicemiaBasal = glicemiaBasal,
    glicemiaPostprandial = glicemiaPostprandial,
    glicemiaAleatoria = glicemiaAleatoria,
    hemoglobinaGlicosilada = hemoglobinaGlicosilada,
    trigliceridos = trigliceridos,
    colesterolTotal = colesterolTotal,
    colesterolHdl = colesterolHdl,
    colesterolLdl = colesterolLdl,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = true
)