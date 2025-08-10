package com.nutrizulia.data.remote.api.auth

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.auth.ForgotPasswordRequestDto
import com.nutrizulia.data.remote.dto.auth.SignInRequestDto
import com.nutrizulia.data.remote.dto.auth.SignInResponseDto
import com.nutrizulia.util.AuthEndpoints
import retrofit2.Response
import retrofit2.http.Body
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

}