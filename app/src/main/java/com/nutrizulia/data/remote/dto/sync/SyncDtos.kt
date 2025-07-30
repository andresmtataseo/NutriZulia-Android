package com.nutrizulia.data.remote.dto.sync

import com.nutrizulia.data.local.entity.collection.*
import java.time.LocalDateTime

/**
 * DTO base para datos de sincronización.
 * Contiene los campos comunes para todas las entidades sincronizables.
 */
data class SyncDataDto(
    val id: String,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean,
    val entityType: String,
    val data: Any? = null
)

/**
 * Request para la operación PUSH.
 * Contiene todas las entidades modificadas localmente que deben enviarse al servidor.
 */
data class SyncPushRequest(
    val pacientes: List<PacienteEntity> = emptyList(),
    val representantes: List<RepresentanteEntity> = emptyList(),
    val consultas: List<ConsultaEntity> = emptyList(),
    val detallesAntropometricos: List<DetalleAntropometricoEntity> = emptyList(),
    val detallesVitales: List<DetalleVitalEntity> = emptyList(),
    val detallesMetabolicos: List<DetalleMetabolicoEntity> = emptyList(),
    val detallesPediatricos: List<DetallePediatricoEntity> = emptyList(),
    val detallesObstetricias: List<DetalleObstetriciaEntity> = emptyList(),
    val evaluacionesAntropometricas: List<EvaluacionAntropometricaEntity> = emptyList(),
    val diagnosticos: List<DiagnosticoEntity> = emptyList(),
    val pacientesRepresentantes: List<PacienteRepresentanteEntity> = emptyList(),
    val actividades: List<ActividadEntity> = emptyList()
)

/**
 * Response para la operación PULL.
 * Contiene todas las entidades que han cambiado en el servidor desde el último timestamp.
 */
data class SyncPullResponse(
    val pacientes: List<PacienteEntity> = emptyList(),
    val representantes: List<RepresentanteEntity> = emptyList(),
    val consultas: List<ConsultaEntity> = emptyList(),
    val detallesAntropometricos: List<DetalleAntropometricoEntity> = emptyList(),
    val detallesVitales: List<DetalleVitalEntity> = emptyList(),
    val detallesMetabolicos: List<DetalleMetabolicoEntity> = emptyList(),
    val detallesPediatricos: List<DetallePediatricoEntity> = emptyList(),
    val detallesObstetricias: List<DetalleObstetriciaEntity> = emptyList(),
    val evaluacionesAntropometricas: List<EvaluacionAntropometricaEntity> = emptyList(),
    val diagnosticos: List<DiagnosticoEntity> = emptyList(),
    val pacientesRepresentantes: List<PacienteRepresentanteEntity> = emptyList(),
    val actividades: List<ActividadEntity> = emptyList(),
    val serverTimestamp: LocalDateTime
)