package com.nutrizulia.data.remote.api.collection

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.collection.*
import com.nutrizulia.util.CollectionSyncEndpoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IBatchSyncService {

    @POST(CollectionSyncEndpoints.SYNC_REPRESENTATIVES)
    suspend fun syncRepresentantesBatch(
        @Body representantes: List<RepresentanteDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_PATIENTS)
    suspend fun syncPacientesBatch(
        @Body pacientes: List<PacienteDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_PATIENT_REPRESENTATIVES)
    suspend fun syncPacientesRepresentantesBatch(
        @Body pacientesRepresentantes: List<PacienteRepresentanteDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_CONSULTATIONS)
    suspend fun syncConsultasBatch(
        @Body consultas: List<ConsultaDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_ANTHROPOMETRIC_DETAILS)
    suspend fun syncDetallesAntropometricosBatch(
        @Body detallesAntropometricos: List<DetalleAntropometricoDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_METABOLIC_DETAILS)
    suspend fun syncDetallesMetabolicosBatch(
        @Body detallesMetabolicos: List<DetalleMetabolicoDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_OBSTETRIC_DETAILS)
    suspend fun syncDetallesObstetriciaBatch(
        @Body detallesObstetricias: List<DetalleObstetriciaDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_PEDIATRIC_DETAILS)
    suspend fun syncDetallesPediatricosBatch(
        @Body detallesPediatricos: List<DetallePediatricoDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_VITAL_DETAILS)
    suspend fun syncDetallesVitalesBatch(
        @Body detallesVitales: List<DetalleVitalDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_DIAGNOSES)
    suspend fun syncDiagnosticosBatch(
        @Body diagnosticos: List<DiagnosticoDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_ANTHROPOMETRIC_EVALUATIONS)
    suspend fun syncEvaluacionesAntropometricasBatch(
        @Body evaluacionesAntropometricas: List<EvaluacionAntropometricaDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>

    @POST(CollectionSyncEndpoints.SYNC_ACTIVITIES)
    suspend fun syncActividadesBatch(
        @Body actividades: List<ActividadDto>
    ): Response<ApiResponseDto<BatchSyncResponseDto>>
}