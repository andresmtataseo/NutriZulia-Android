package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ActividadRepository
import com.nutrizulia.domain.model.collection.Actividad
import javax.inject.Inject

class GetActividadById @Inject constructor(
    private val repository: ActividadRepository
) {
    suspend operator fun invoke(id: String, usuarioInstitucionId: Int) : Actividad? {
        return repository.findById(id, usuarioInstitucionId)
    }
}