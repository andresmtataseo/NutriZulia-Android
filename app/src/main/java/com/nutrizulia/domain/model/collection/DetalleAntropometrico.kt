package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.DetalleAntropometricoEntity
import java.time.LocalDateTime

data class DetalleAntropometrico(
    val id: String,
    val consultaId: String,
    val peso: Double?,
    val altura: Double?,
    val talla: Double?,
    val circunferenciaBraquial: Double?,
    val circunferenciaCadera: Double?,
    val circunferenciaCintura: Double?,
    val perimetroCefalico: Double?,
    val pliegueTricipital: Double?,
    val pliegueSubescapular: Double?,
    var updatedAt: LocalDateTime,
    var isDeleted: Boolean,
    var isSynced: Boolean
)

fun DetalleAntropometricoEntity.toDomain() = DetalleAntropometrico(
    id = id,
    consultaId = consultaId,
    peso = peso,
    altura = altura,
    talla = talla,
    circunferenciaBraquial = circunferenciaBraquial,
    circunferenciaCadera = circunferenciaCadera,
    circunferenciaCintura = circunferenciaCintura,
    perimetroCefalico = perimetroCefalico,
    pliegueTricipital = pliegueTricipital,
    pliegueSubescapular = pliegueSubescapular,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = isSynced
)