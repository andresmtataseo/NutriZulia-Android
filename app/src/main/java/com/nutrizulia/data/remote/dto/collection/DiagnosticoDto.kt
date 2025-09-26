package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity
import java.time.LocalDateTime

data class DiagnosticoDto(
    @SerializedName("id") val id: String,
    @SerializedName("consulta_id") val consultaId: String,
    @SerializedName("riesgo_biologico_id") val riesgoBiologicoId: Int,
    @SerializedName("enfermedad_id") val enfermedadId: Int?,
    @SerializedName("is_principal") val isPrincipal: Boolean,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean,
)

fun DiagnosticoDto.toEntity() = DiagnosticoEntity(
    id = id,
    consultaId = consultaId,
    riesgoBiologicoId = riesgoBiologicoId,
    enfermedadId = enfermedadId,
    isPrincipal = isPrincipal,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = true
)