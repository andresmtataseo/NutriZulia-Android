package com.nutrizulia.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequestDto (
    @SerializedName("cedula") val cedula: String
)