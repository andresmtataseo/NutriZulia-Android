package com.nutrizulia.data.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("id_user") val idUser: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("broadcast_time") val broadcastTime: Int,
    @SerializedName("expiry_time") val expiryTime: Int,
    @SerializedName("token") val token: String
)