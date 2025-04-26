package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.MunicipioRepository
import com.nutrizulia.domain.model.Municipio
import javax.inject.Inject

class GetMunicipio @Inject constructor(
    private val repository: MunicipioRepository
) {

    suspend operator fun invoke(codEntidad: String,codMunicipio: String): Municipio {
        val municipio = repository.getMunicipio(codEntidad, codMunicipio)
        return municipio
    }

}