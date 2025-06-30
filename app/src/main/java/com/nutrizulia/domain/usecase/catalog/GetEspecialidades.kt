package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.EspecialidadRepository
import com.nutrizulia.domain.model.catalog.Especialidad
import javax.inject.Inject

class GetEspecialidades @Inject constructor(
    private val repository: EspecialidadRepository
) {
    suspend operator fun invoke(): List<Especialidad> {
        return repository.findAll()
    }
}