package com.nutrizulia.data.remote.api.auth

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.auth.ChangePasswordRequestDto
import com.nutrizulia.data.remote.dto.auth.CheckAuthResponseDto
import com.nutrizulia.data.remote.dto.auth.ForgotPasswordRequestDto
import com.nutrizulia.data.remote.dto.auth.SignInRequestDto
import com.nutrizulia.data.remote.dto.auth.SignInResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IAuthService {

    @POST("/auth/sign-in")
    suspend fun signIn(
        @Body request: SignInRequestDto
    ): Response<SignInResponseDto>

    @POST("/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequestDto
    ): Response<ApiResponseDto<Any>>

    @POST("/auth/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto,
    ): Response<ApiResponseDto<Any>>

    @GET("/auth/check-auth")
    suspend fun checkAuth(
    ): Response<ApiResponseDto<CheckAuthResponseDto>>

}