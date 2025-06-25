package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.DetallePediatricoEntity
import com.nutrizulia.data.local.enum.TipoLactancia
import java.time.LocalDateTime

data class DetallePedriatico(
    val id: String,
    val consultaId: String,
    val usaBiberon: Boolean?,
    val tipoLactancia: TipoLactancia?,
    val updatedAt: LocalDateTime
)

fun DetallePediatricoEntity.toDomain() = DetallePedriatico(
    id = id,
    consultaId = consultaId,
    usaBiberon = usaBiberon,
    tipoLactancia = tipoLactancia,
    updatedAt = updatedAt
)