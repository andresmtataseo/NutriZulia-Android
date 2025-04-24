package com.nutrizulia.data.remote.api

import com.nutrizulia.data.model.ComunidadResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ComunidadApiClient {

    @GET("listadoComunidad")
    suspend fun getComunidades(
        @Query("token") token: String,
        @Query("codEntidad") codEntidad: String,
        @Query("codMunicipio") codMunicipio: String,
        @Query("codParroquia") codParroquia: String
    ): ComunidadResponse

}