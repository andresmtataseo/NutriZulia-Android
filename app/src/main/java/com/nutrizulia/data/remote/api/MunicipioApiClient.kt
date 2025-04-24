package com.nutrizulia.data.remote.api

import com.nutrizulia.data.model.MunicipioResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MunicipioApiClient {

    @GET("listadoMunicipio")
    suspend fun getMunicipios(
        @Query("token") token: String,
        @Query("codEntidad") codEntidad: String
    ): MunicipioResponse

}