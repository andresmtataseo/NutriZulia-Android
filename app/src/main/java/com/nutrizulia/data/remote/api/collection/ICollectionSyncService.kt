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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ICollectionSyncService {

    @POST("collection/sync/actividades")
    suspend fun syncActividades(
        @Body actividades: List<ActividadDto>
    ): Response<ApiResponseDto<List<ActividadDto>>>

    @POST("collection/sync/consultas")
    suspend fun syncConsultas(
        @Body consultas: List<ConsultaDto>
    ): Response<ApiResponseDto<List<ConsultaDto>>>

    @POST("collection/sync/detallesAntropometricos")
    suspend fun syncDetallesAntropometricos(
        @Body detallesAntropometricos: List<DetalleAntropometricoDto>
    ): Response<ApiResponseDto<List<DetalleAntropometricoDto>>>

    @POST("collection/sync/detallesMetabolicos")
    suspend fun syncDetallesMetabolicos(
        @Body detallesMetabolicos: List<DetalleMetabolicoDto>
    ): Response<ApiResponseDto<List<DetalleMetabolicoDto>>>

    @POST("collection/sync/detallesObstetricias")
    suspend fun syncDetallesObstetricias(
        @Body detallesObstetricias: List<DetalleObstetriciaDto>
    ): Response<ApiResponseDto<List<DetalleObstetriciaDto>>>

    @POST("collection/sync/detallesPediatricos")
    suspend fun syncDetallesPediatricos(
        @Body detallesPediatricos: List<DetallePediatricoDto>
    ): Response<ApiResponseDto<List<DetallePediatricoDto>>>

    @POST("collection/sync/detallesVitales")
    suspend fun syncDetallesVitales(
        @Body detallesVitales: List<DetalleVitalDto>
    ): Response<ApiResponseDto<List<DetalleVitalDto>>>

    @POST("collection/sync/diagnosticos")
    suspend fun syncDiagnosticoDto(
        @Body diagnosticoDto: List<DiagnosticoDto>
    ): Response<ApiResponseDto<List<DiagnosticoDto>>>

    @POST("collection/sync/evaluacionesAntropometricas")
    suspend fun syncEvaluacionesAntropometricas(
        @Body evaluacionesAntropometricas: List<EvaluacionAntropometricaDto>
    ): Response<ApiResponseDto<List<EvaluacionAntropometricaDto>>>

    @POST("collection/sync/pacientes")
    suspend fun syncPacientes(
        @Body pacientes: List<PacienteDto>
    ): Response<ApiResponseDto<List<PacienteDto>>>

    @POST("collection/sync/pacientesRepresentantes")
    suspend fun syncPacientesRepresentantes(
        @Body pacientesRepresentantes: List<PacienteRepresentanteDto>
    ): Response<ApiResponseDto<List<PacienteRepresentanteDto>>>

    @POST("collection/sync/representantes")
    suspend fun syncRepresentantes(
        @Body representantes: List<RepresentanteDto>
    ): Response<ApiResponseDto<List<RepresentanteDto>>>

}