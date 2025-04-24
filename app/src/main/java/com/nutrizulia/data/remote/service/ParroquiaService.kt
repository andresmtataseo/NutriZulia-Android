package com.nutrizulia.data.remote.service

import com.nutrizulia.data.model.ParroquiaModel
import com.nutrizulia.data.remote.api.ParroquiaApiClient
import javax.inject.Inject

class ParroquiaService @Inject constructor(
    private val api: ParroquiaApiClient
){

    suspend fun getParroquias(token: String, codEntidad: String, codMunicipio: String): List<ParroquiaModel> {
        return api.getParroquias(token, codEntidad, codMunicipio).data
    }

}