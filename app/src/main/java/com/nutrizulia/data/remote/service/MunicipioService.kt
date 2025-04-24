package com.nutrizulia.data.remote.service

import com.nutrizulia.data.model.MunicipioModel
import com.nutrizulia.data.remote.api.MunicipioApiClient
import javax.inject.Inject

class MunicipioService @Inject constructor(
    private val api: MunicipioApiClient
) {

    suspend fun getMunicipios(token: String, codEntidad: String): List<MunicipioModel> {
        return api.getMunicipios(token, codEntidad).data
    }

}