package com.nutrizulia.data.remote.api

import com.nutrizulia.data.model.LoginModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginSegenApiClient {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("usuario") usuario: String,
        @Field("clave") clave: String
    ): LoginModel

}