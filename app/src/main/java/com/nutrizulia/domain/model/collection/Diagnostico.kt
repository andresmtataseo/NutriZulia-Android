package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity
import java.time.LocalDateTime

data class Diagnostico(
    val id: String,
    val consultaId: String,
    val riesgoBiologicoId: Int,
    val enfermedadId: Int?,
    val isPrincipal: Boolean,
    val updatedAt: LocalDateTime
)

fun DiagnosticoEntity.toDomain() = Diagnostico(
    id = id,
    consultaId = consultaId,
    riesgoBiologicoId = riesgoBiologicoId,
    enfermedadId = enfermedadId,
    isPrincipal = isPrincipal,
    updatedAt = updatedAt
)