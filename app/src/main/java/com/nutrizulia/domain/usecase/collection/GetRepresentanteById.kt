package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.RepresentanteRepository
import com.nutrizulia.domain.model.collection.Representante
import javax.inject.Inject

class GetRepresentanteById @Inject constructor(
    private val repository: RepresentanteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int, representanteId: String): Representante? {
        return repository.findById(usuarioInstitucionId, representanteId)
    }
}