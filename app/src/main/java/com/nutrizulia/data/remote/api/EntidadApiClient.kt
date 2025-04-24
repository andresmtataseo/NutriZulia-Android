package com.nutrizulia.data.remote.api

import com.nutrizulia.data.model.EntidadResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EntidadApiClient {

    @GET("listadoEntidad")
    suspend fun getEntidades(
        @Query("token") token: String
    ): EntidadResponse

}