package com.nutrizulia.data.remote.api.user

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.user.SaveEmailRequestDto
import com.nutrizulia.data.remote.dto.user.SavePhoneRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface IUserService {

//    @GET("/catalog/v1/roles")  // Asegúrate de que la ruta sea correcta
//    suspend fun getRoles(): Response<List<RolDto>>

    @GET("api/v1/user/check-phone")
    suspend fun checkPhoneNumber(
        @Query("phone") phone : String
    ): Response<Boolean>

    @GET("api/v1/user/check-email")
    suspend fun checkEmail(
        @Query("email") email : String
    ): Response<Boolean>

    @PUT("api/v1/user/save-phone")
    suspend fun savePhone(
        @Body request: SavePhoneRequestDto
    ): Response<ApiResponseDto<Void>>

    @PUT("api/v1/user/save-email")
    suspend fun saveEmail(
        @Body request: SaveEmailRequestDto
    ): Response<ApiResponseDto<Void>>

}