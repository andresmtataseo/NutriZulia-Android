package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.RepresentanteRepository
import com.nutrizulia.domain.model.collection.Representante
import javax.inject.Inject

class GetRepresentantes @Inject constructor(
    private val repository: RepresentanteRepository
) {
    suspend operator fun invoke(idUsuarioInstitucion: Int): List<Representante> {
        return repository.findAll(idUsuarioInstitucion)

    }
}