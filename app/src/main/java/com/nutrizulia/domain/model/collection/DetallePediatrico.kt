package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.DetallePediatricoEntity
import com.nutrizulia.data.local.enum.TipoLactancia
import java.time.LocalDateTime

data class DetallePediatrico(
    val id: String,
    val consultaId: String,
    val usaBiberon: Boolean?,
    val tipoLactancia: TipoLactancia?,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean
)

fun DetallePediatricoEntity.toDomain() = DetallePediatrico(
    id = id,
    consultaId = consultaId,
    usaBiberon = usaBiberon,
    tipoLactancia = tipoLactancia,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)