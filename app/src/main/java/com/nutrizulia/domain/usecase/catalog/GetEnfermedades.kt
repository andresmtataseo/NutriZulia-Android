package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.EnfermedadRepository
import com.nutrizulia.domain.model.catalog.Enfermedad
import javax.inject.Inject

class GetEnfermedades @Inject constructor(
    private val repository: EnfermedadRepository
) {
    suspend operator fun invoke(genero: String, nombre: String): List<Enfermedad> {
        return repository.findAllByGeneroAndNombreLike(genero, nombre)
    }
}