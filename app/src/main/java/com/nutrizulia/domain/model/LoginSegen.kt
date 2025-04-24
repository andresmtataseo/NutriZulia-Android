package com.nutrizulia.domain.model

import com.nutrizulia.data.model.LoginModel

data class LoginSegen(
    val idUser: String?,
    val name: String?,
    val email: String?,
    val broadcastTime: Int?,
    val expiryTime: Int?,
    val token: String?
)

fun LoginModel.toDomain() = LoginSegen(
    idUser,
    name,
    email,
    broadcastTime,
    expiryTime,
    token
)