package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.data.repository.collection.ActividadRepository
import javax.inject.Inject

class GetActividadConTipoById @Inject constructor(
    private val repository: ActividadRepository
) {
    suspend operator fun invoke(id: String, idUsuarioInstitucion: Int): ActividadConTipo? {
        return repository.findByIdActividadConTipo(id, idUsuarioInstitucion)
    }

}