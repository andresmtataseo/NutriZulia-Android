package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.ParroquiaRepository
import com.nutrizulia.domain.model.Parroquia
import javax.inject.Inject

class GetParroquia @Inject constructor(
    private val repository: ParroquiaRepository
){

    suspend operator fun invoke(codEntidad: String, codMunicipio: String, codParroquia: String): Parroquia {
        val parroquia = repository.getParroquia(codEntidad, codMunicipio, codParroquia)
        return parroquia
    }

}