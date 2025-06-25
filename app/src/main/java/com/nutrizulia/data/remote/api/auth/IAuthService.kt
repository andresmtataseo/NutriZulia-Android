package com.nutrizulia.data.remote.api.auth

import com.nutrizulia.data.remote.dto.auth.SignInRequestDto
import com.nutrizulia.data.remote.dto.auth.SignInResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthService {

    @POST("/auth/sign-in")
    suspend fun signIn(
        @Body request: SignInRequestDto
    ): Response<SignInResponseDto>

}