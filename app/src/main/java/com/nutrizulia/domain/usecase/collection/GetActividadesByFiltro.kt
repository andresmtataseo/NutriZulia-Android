package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.data.repository.collection.ActividadRepository
import com.nutrizulia.domain.model.collection.Actividad
import javax.inject.Inject

class GetActividadesByFiltro @Inject constructor(
    private val repository: ActividadRepository
) {
    suspend operator fun invoke(idUsuarioInstitucion: Int, filtro: String): List<ActividadConTipo> {
        return repository.findAllByFiltro(idUsuarioInstitucion, filtro)
    }
}