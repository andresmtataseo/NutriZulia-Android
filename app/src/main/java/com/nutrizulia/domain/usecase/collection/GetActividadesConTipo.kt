package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.data.repository.collection.ActividadRepository
import javax.inject.Inject

class GetActividadesConTipo @Inject constructor(
    private val repository: ActividadRepository
){
    suspend operator fun invoke(idUsuarioInstitucion: Int): List<ActividadConTipo> {
        return repository.findAll(idUsuarioInstitucion)
    }

}