package com.nutrizulia.data.remote.api.collection

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.collection.ActividadDto
import com.nutrizulia.data.remote.dto.collection.ConsultaDto
import com.nutrizulia.data.remote.dto.collection.DetalleAntropometricoDto
import com.nutrizulia.data.remote.dto.collection.DetalleMetabolicoDto
import com.nutrizulia.data.remote.dto.collection.DetalleObstetriciaDto
import com.nutrizulia.data.remote.dto.collection.DetallePediatricoDto
import com.nutrizulia.data.remote.dto.collection.DetalleVitalDto
import com.nutrizulia.data.remote.dto.collection.DiagnosticoDto
import com.nutrizulia.data.remote.dto.collection.EvaluacionAntropometricaDto
import com.nutrizulia.data.remote.dto.collection.PacienteDto
import com.nutrizulia.data.remote.dto.collection.PacienteRepresentanteDto
import com.nutrizulia.data.remote.dto.collection.RepresentanteDto
import com.nutrizulia.util.CollectionSyncEndpoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ICollectionSyncService {

    @POST(CollectionSyncEndpoints.SYNC_ACTIVITIES)
    suspend fun syncActividades(
        @Body actividades: List<ActividadDto>
    ): Response<ApiResponseDto<List<ActividadDto>>>

    @POST(CollectionSyncEndpoints.SYNC_CONSULTATIONS)
    suspend fun syncConsultas(
        @Body consultas: List<ConsultaDto>
    ): Response<ApiResponseDto<List<ConsultaDto>>>

    @POST(CollectionSyncEndpoints.SYNC_ANTHROPOMETRIC_DETAILS)
    suspend fun syncDetallesAntropometricos(
        @Body detallesAntropometricos: List<DetalleAntropometricoDto>
    ): Response<ApiResponseDto<List<DetalleAntropometricoDto>>>

    @POST(CollectionSyncEndpoints.SYNC_METABOLIC_DETAILS)
    suspend fun syncDetallesMetabolicos(
        @Body detallesMetabolicos: List<DetalleMetabolicoDto>
    ): Response<ApiResponseDto<List<DetalleMetabolicoDto>>>

    @POST(CollectionSyncEndpoints.SYNC_OBSTETRIC_DETAILS)
    suspend fun syncDetallesObstetricias(
        @Body detallesObstetricias: List<DetalleObstetriciaDto>
    ): Response<ApiResponseDto<List<DetalleObstetriciaDto>>>

    @POST(CollectionSyncEndpoints.SYNC_PEDIATRIC_DETAILS)
    suspend fun syncDetallesPediatricos(
        @Body detallesPediatricos: List<DetallePediatricoDto>
    ): Response<ApiResponseDto<List<DetallePediatricoDto>>>

    @POST(CollectionSyncEndpoints.SYNC_VITAL_DETAILS)
    suspend fun syncDetallesVitales(
        @Body detallesVitales: List<DetalleVitalDto>
    ): Response<ApiResponseDto<List<DetalleVitalDto>>>

    @POST(CollectionSyncEndpoints.SYNC_DIAGNOSES)
    suspend fun syncDiagnosticoDto(
        @Body diagnosticoDto: List<DiagnosticoDto>
    ): Response<ApiResponseDto<List<DiagnosticoDto>>>

    @POST(CollectionSyncEndpoints.SYNC_ANTHROPOMETRIC_EVALUATIONS)
    suspend fun syncEvaluacionesAntropometricas(
        @Body evaluacionesAntropometricas: List<EvaluacionAntropometricaDto>
    ): Response<ApiResponseDto<List<EvaluacionAntropometricaDto>>>

    @POST(CollectionSyncEndpoints.SYNC_PATIENTS)
    suspend fun syncPacientes(
        @Body pacientes: List<PacienteDto>
    ): Response<ApiResponseDto<List<PacienteDto>>>

    @POST(CollectionSyncEndpoints.SYNC_PATIENT_REPRESENTATIVES)
    suspend fun syncPacientesRepresentantes(
        @Body pacientesRepresentantes: List<PacienteRepresentanteDto>
    ): Response<ApiResponseDto<List<PacienteRepresentanteDto>>>

    @POST(CollectionSyncEndpoints.SYNC_REPRESENTATIVES)
    suspend fun syncRepresentantes(
        @Body representantes: List<RepresentanteDto>
    ): Response<ApiResponseDto<List<RepresentanteDto>>>

}