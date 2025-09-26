package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.DetallePediatricoEntity
import com.nutrizulia.data.local.enum.TipoLactancia
import java.time.LocalDateTime

data class DetallePediatricoDto(
    @SerializedName("id") val id: String,
    @SerializedName("consulta_id") val consultaId: String,
    @SerializedName("usa_biberon") val usaBiberon: Boolean?,
    @SerializedName("tipo_lactancia") val tipoLactancia: TipoLactancia?,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean,
)

fun DetallePediatricoDto.toEntity() = DetallePediatricoEntity(
    id = id,
    consultaId = consultaId,
    usaBiberon = usaBiberon,
    tipoLactancia = tipoLactancia,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = true
)