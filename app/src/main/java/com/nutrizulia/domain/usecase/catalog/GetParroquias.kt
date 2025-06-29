package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParroquiaRepository
import com.nutrizulia.domain.model.catalog.Parroquia
import javax.inject.Inject

class GetParroquias @Inject constructor(
    private val repository: ParroquiaRepository
) {
    suspend operator fun invoke(idMunicipio: Int): List<Parroquia> {
        return repository.findAllByMunicipioId(idMunicipio)
    }
}
