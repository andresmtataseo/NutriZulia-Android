package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DetalleAntropometricoRepository
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import javax.inject.Inject

class GetDetalleAntropometricoByConsultaId @Inject constructor(
    private val repository: DetalleAntropometricoRepository
) {
    suspend operator fun invoke(consultaId: String) : DetalleAntropometrico? {
        return repository.findByConsultaId(consultaId)
    }
}