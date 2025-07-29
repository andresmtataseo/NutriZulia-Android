package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.PacienteRepresentanteEntity
import java.time.LocalDateTime

data class PacienteRepresentante(
    val id: String,
    val usuarioInstitucionId: Int,
    val pacienteId: String,
    val representanteId: String,
    val parentescoId: Int,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean
)

fun PacienteRepresentanteEntity.toDomain() = PacienteRepresentante(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    pacienteId = pacienteId,
    representanteId = representanteId,
    parentescoId = parentescoId,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)