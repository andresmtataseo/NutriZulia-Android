package com.nutrizulia.data.remote.service

import com.nutrizulia.data.model.LoginModel
import com.nutrizulia.data.remote.api.LoginSegenApiClient
import javax.inject.Inject

class LoginSegenService @Inject constructor(
    private val api: LoginSegenApiClient
) {

    suspend fun loginSegen(usuario: String, clave: String): LoginModel {
        val loginModel = api.login(usuario, clave)
        return loginModel
    }

}