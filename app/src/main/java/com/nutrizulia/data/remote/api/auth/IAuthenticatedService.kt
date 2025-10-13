package com.nutrizulia.data.remote.api.auth

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.auth.ChangePasswordRequestDto
import com.nutrizulia.data.remote.dto.auth.CheckAuthResponseDto
import com.nutrizulia.util.AuthEndpoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IAuthenticatedService {

    @POST(AuthEndpoints.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto,
    ): Response<ApiResponseDto<Any>>

    @GET(AuthEndpoints.CHECK_AUTH)
    suspend fun checkAuth(
    ): Response<ApiResponseDto<CheckAuthResponseDto>>

    @POST(AuthEndpoints.LOGOUT)
    suspend fun logout(
    ): Response<ApiResponseDto<Any>>
}