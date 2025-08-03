package com.nutrizulia.data.remote.api.collection

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.collection.PacienteRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IPacienteService {

    @POST("collection/sync/pacientes")
    suspend fun syncPacientes(
        @Body pacientes: List<PacienteRequestDto>
    ): Response<ApiResponseDto<List<PacienteRequestDto>>>

}