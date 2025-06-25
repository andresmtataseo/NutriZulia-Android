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
    val updatedAt: LocalDateTime
)

fun DetalleObstetriciaEntity.toDomain() = DetalleObstetricia(
    id = id,
    consultaId = consultaId,
    estaEmbarazada = estaEmbarazada,
    fechaUltimaMenstruacion = fechaUltimaMenstruacion,
    semanasGestacion = semanasGestacion,
    pesoPreEmbarazo = pesoPreEmbarazo,
    updatedAt = updatedAt
)