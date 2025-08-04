package com.nutrizulia.data.remote.api.auth

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.auth.ChangePasswordRequestDto
import com.nutrizulia.data.remote.dto.auth.CheckAuthResponseDto
import com.nutrizulia.data.remote.dto.auth.ForgotPasswordRequestDto
import com.nutrizulia.data.remote.dto.auth.SignInRequestDto
import com.nutrizulia.data.remote.dto.auth.SignInResponseDto
import com.nutrizulia.util.AuthEndpoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IAuthService {

    @POST(AuthEndpoints.SIGN_IN)
    suspend fun signIn(
        @Body request: SignInRequestDto
    ): Response<ApiResponseDto<SignInResponseDto>>

    @POST(AuthEndpoints.FORGOT_PASSWORD)
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequestDto
    ): Response<ApiResponseDto<Any>>

    @POST(AuthEndpoints.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto,
    ): Response<ApiResponseDto<Any>>

    @GET(AuthEndpoints.CHECK_AUTH)
    suspend fun checkAuth(
    ): Response<ApiResponseDto<CheckAuthResponseDto>>

}