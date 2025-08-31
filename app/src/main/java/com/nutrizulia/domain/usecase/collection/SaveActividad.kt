package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ActividadRepository
import com.nutrizulia.domain.model.collection.Actividad
import javax.inject.Inject

class SaveActividad @Inject constructor(
    private val repository: ActividadRepository
) {
    suspend operator fun invoke(actividad: Actividad) {
        repository.upsert(actividad)
    }

}