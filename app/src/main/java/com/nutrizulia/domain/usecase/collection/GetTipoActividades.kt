package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.catalog.TipoActividadRepository
import com.nutrizulia.domain.model.catalog.TipoActividad
import javax.inject.Inject

class GetTipoActividades @Inject constructor(
    private val repository: TipoActividadRepository
) {
    suspend operator fun invoke(): List<TipoActividad> {
        return repository.findAll()
    }
}