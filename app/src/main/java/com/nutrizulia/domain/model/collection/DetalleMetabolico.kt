package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.DetalleMetabolicoEntity
import java.time.LocalDateTime

data class DetalleMetabolico(
    val id: String,
    val consultaId: String,
    val glicemiaBasal: Int?,
    val glicemiaPostprandial: Int?,
    val glicemiaAleatoria: Int?,
    val hemoglobinaGlicosilada: Double?,
    val trigliceridos: Int?,
    val colesterolTotal: Int?,
    val colesterolHdl: Int?,
    val colesterolLdl: Int?,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean
)

fun DetalleMetabolicoEntity.toDomain() = DetalleMetabolico(
    id = id,
    consultaId = consultaId,
    glicemiaBasal = glicemiaBasal,
    glicemiaPostprandial = glicemiaPostprandial,
    glicemiaAleatoria = glicemiaAleatoria,
    hemoglobinaGlicosilada = hemoglobinaGlicosilada,
    trigliceridos = trigliceridos,
    colesterolTotal = colesterolTotal,
    colesterolHdl = colesterolHdl,
    colesterolLdl = colesterolLdl,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)