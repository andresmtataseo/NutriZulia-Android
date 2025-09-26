package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.PacienteRepresentanteEntity
import java.time.LocalDateTime

data class PacienteRepresentanteDto(
    @SerializedName("id") val id: String,
    @SerializedName("usuario_institucion_id") val usuarioInstitucionId: Int,
    @SerializedName("paciente_id") val pacienteId: String,
    @SerializedName("representante_id") val representanteId: String,
    @SerializedName("parentesco_id") val parentescoId: Int,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean
)

fun PacienteRepresentanteDto.toEntity() = PacienteRepresentanteEntity(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    pacienteId = pacienteId,
    representanteId = representanteId,
    parentescoId = parentescoId,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = true
)