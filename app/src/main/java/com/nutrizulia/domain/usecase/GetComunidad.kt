package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.ComunidadRepository
import com.nutrizulia.domain.model.Comunidad
import javax.inject.Inject

class GetComunidad @Inject constructor(
    private val repository: ComunidadRepository
) {

    suspend operator fun invoke(codEntidad: String, codMunicipio: String, codParroquia: String, idComunidad: String): Comunidad {
        return repository.getComunidad(codEntidad, codMunicipio, codParroquia, idComunidad)
    }

}