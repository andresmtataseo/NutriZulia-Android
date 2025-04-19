package com.nutrizulia.data.remote.api

import com.nutrizulia.data.model.EntidadResponse
import com.nutrizulia.data.model.MunicipioResponse
import com.nutrizulia.data.model.ParroquiaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UbicacionApiClient {

//    @FormUrlEncoded
//    @POST("login")
//    suspend fun login(
//        @Field("usuario") usuario: String,
//        @Field("clave") clave: String
//    ): LoginResponse

    @GET("listadoEntidad")
    suspend fun getEntidades(
        @Query("token") token: String
    ): EntidadResponse

    @GET("listadoMunicipio")
    suspend fun getMunicipios(
        @Query("token") token: String,
        @Query("codEntidad") codEntidad: String
    ): MunicipioResponse

    @GET("listadoParroquia")
    suspend fun getParroquias(
        @Query("token") token: String,
        @Query("codEntidad") codEntidad: String,
        @Query("codMunicipio") codMunicipio: String
    ): ParroquiaResponse

}