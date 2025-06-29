package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.MunicipioRepository
import com.nutrizulia.domain.model.catalog.Municipio
import javax.inject.Inject

class GetMunicipios @Inject constructor(
    private val repository: MunicipioRepository
) {
    suspend operator fun invoke(idEstado: Int): List<Municipio> {
        return repository.findAllByEstadoId(idEstado)
    }
}