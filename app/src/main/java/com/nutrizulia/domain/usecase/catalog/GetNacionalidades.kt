package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.NacionalidadRepository
import com.nutrizulia.domain.model.catalog.Nacionalidad
import javax.inject.Inject

class GetNacionalidades @Inject constructor(
    private val repository: NacionalidadRepository
) {
    suspend operator fun invoke(): List<Nacionalidad> {
        return repository.findAll()
    }
}
