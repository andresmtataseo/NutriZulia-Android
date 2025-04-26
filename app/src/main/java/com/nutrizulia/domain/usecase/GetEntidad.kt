package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.EntidadRepository
import com.nutrizulia.domain.model.Entidad
import javax.inject.Inject

class GetEntidad @Inject constructor(
  private val repository: EntidadRepository
) {

    suspend operator fun invoke(codEntidad: String): Entidad {
        val entidad = repository.getEntidad(codEntidad)
        return entidad
    }

}