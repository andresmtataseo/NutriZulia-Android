package com.nutrizulia.data.remote.api

import com.nutrizulia.data.model.ParroquiaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ParroquiaApiClient {

    @GET("listadoParroquia")
    suspend fun getParroquias(
        @Query("token") token: String,
        @Query("codEntidad") codEntidad: String,
        @Query("codMunicipio") codMunicipio: String
    ): ParroquiaResponse

}