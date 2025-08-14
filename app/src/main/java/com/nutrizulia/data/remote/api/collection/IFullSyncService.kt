package com.nutrizulia.data.remote.api.collection

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.collection.*
import com.nutrizulia.util.CollectionSyncEndpoints
import retrofit2.Response
import retrofit2.http.GET

/**
 * Servicio para sincronización completa (full sync) de todas las colecciones
 * Utiliza los endpoints GET /sync/{tabla}/full del backend
 */
interface IFullSyncService {

    @GET(CollectionSyncEndpoints.FULL_SYNC_REPRESENTATIVES)
    suspend fun getFullSyncRepresentantes(): Response<ApiResponseDto<FullSyncResponseDto<RepresentanteDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_PATIENTS)
    suspend fun getFullSyncPacientes(): Response<ApiResponseDto<FullSyncResponseDto<PacienteDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_PATIENT_REPRESENTATIVES)
    suspend fun getFullSyncPacientesRepresentantes(): Response<ApiResponseDto<FullSyncResponseDto<PacienteRepresentanteDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_CONSULTATIONS)
    suspend fun getFullSyncConsultas(): Response<ApiResponseDto<FullSyncResponseDto<ConsultaDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_ANTHROPOMETRIC_DETAILS)
    suspend fun getFullSyncDetallesAntropometricos(): Response<ApiResponseDto<FullSyncResponseDto<DetalleAntropometricoDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_METABOLIC_DETAILS)
    suspend fun getFullSyncDetallesMetabolicos(): Response<ApiResponseDto<FullSyncResponseDto<DetalleMetabolicoDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_OBSTETRIC_DETAILS)
    suspend fun getFullSyncDetallesObstetricias(): Response<ApiResponseDto<FullSyncResponseDto<DetalleObstetriciaDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_PEDIATRIC_DETAILS)
    suspend fun getFullSyncDetallesPediatricos(): Response<ApiResponseDto<FullSyncResponseDto<DetallePediatricoDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_VITAL_DETAILS)
    suspend fun getFullSyncDetallesVitales(): Response<ApiResponseDto<FullSyncResponseDto<DetalleVitalDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_DIAGNOSES)
    suspend fun getFullSyncDiagnosticos(): Response<ApiResponseDto<FullSyncResponseDto<DiagnosticoDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_ANTHROPOMETRIC_EVALUATIONS)
    suspend fun getFullSyncEvaluacionesAntropometricas(): Response<ApiResponseDto<FullSyncResponseDto<EvaluacionAntropometricaDto>>>

    @GET(CollectionSyncEndpoints.FULL_SYNC_ACTIVITIES)
    suspend fun getFullSyncActividades(): Response<ApiResponseDto<FullSyncResponseDto<ActividadDto>>>
}