package com.nutrizulia.data.repository

import com.nutrizulia.data.remote.service.LoginSegenService
import com.nutrizulia.domain.model.LoginSegen
import com.nutrizulia.domain.model.toDomain
import com.nutrizulia.util.Constantes.SEGEN_CLAVE
import com.nutrizulia.util.Constantes.SEGEN_USUARIO
import javax.inject.Inject

class LoginSegenRepository @Inject constructor(
    private val api: LoginSegenService
) {

    suspend fun loginSegen(): LoginSegen {
        return api.loginSegen(SEGEN_USUARIO, SEGEN_CLAVE).toDomain()
    }

}