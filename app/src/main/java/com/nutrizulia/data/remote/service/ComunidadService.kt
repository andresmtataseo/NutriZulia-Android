package com.nutrizulia.data.remote.service

import com.nutrizulia.data.model.ComunidadModel
import com.nutrizulia.data.remote.api.ComunidadApiClient
import javax.inject.Inject

class ComunidadService @Inject constructor(
    private val api: ComunidadApiClient
){

    suspend fun getComunidades(token: String, codEntidad: String, codMunicipio: String, codParroquia: String): List<ComunidadModel> {
        return api.getComunidades(token, codEntidad, codMunicipio, codParroquia).data
    }

}