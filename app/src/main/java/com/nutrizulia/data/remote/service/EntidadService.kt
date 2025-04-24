package com.nutrizulia.data.remote.service

import com.nutrizulia.data.model.EntidadModel
import com.nutrizulia.data.remote.api.EntidadApiClient
import javax.inject.Inject

class EntidadService @Inject constructor(
    private val api: EntidadApiClient
) {

    suspend fun getEntidades(token: String): List<EntidadModel> {
       return api.getEntidades(token).data
    }

}