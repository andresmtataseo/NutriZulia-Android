package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.DetalleObstetriciaEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class DetalleObstetricia(
    val id: String,
    val consultaId: String,
    val estaEmbarazada: Boolean?,
    val fechaUltimaMenstruacion: LocalDate?,
    val semanasGestacion: Int?,
    val pesoPreEmbarazo: Double?,
    var updatedAt: LocalDateTime,
    var isDeleted: Boolean,
    var isSynced: Boolean
)

fun DetalleObstetriciaEntity.toDomain() = DetalleObstetricia(
    id = id,
    consultaId = consultaId,
    estaEmbarazada = estaEmbarazada,
    fechaUltimaMenstruacion = fechaUltimaMenstruacion,
    semanasGestacion = semanasGestacion,
    pesoPreEmbarazo = pesoPreEmbarazo,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = isSynced
)