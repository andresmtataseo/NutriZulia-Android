package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.DetalleObstetriciaEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class DetalleObstetriciaDto(
    @SerializedName("id") val id: String,
    @SerializedName("consulta_id") val consultaId: String,
    @SerializedName("esta_embarazada") val estaEmbarazada: Boolean?,
    @SerializedName("fecha_ultima_menstruacion") val fechaUltimaMenstruacion: LocalDate?,
    @SerializedName("semanas_gestacion") val semanasGestacion: Int?,
    @SerializedName("peso_pre_embarazo") val pesoPreEmbarazo: Double?,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean,
)

fun DetalleObstetriciaDto.toEntity() = DetalleObstetriciaEntity(
    id = id,
    consultaId = consultaId,
    estaEmbarazada = estaEmbarazada,
    fechaUltimaMenstruacion = fechaUltimaMenstruacion,
    semanasGestacion = semanasGestacion,
    pesoPreEmbarazo = pesoPreEmbarazo,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = true
)