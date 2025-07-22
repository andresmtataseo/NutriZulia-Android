package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParametroCrecimientoPediatricoLongitudRepository
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoPediatricoLongitud
import javax.inject.Inject

class GetParametroCrecimientoPediatricoLongitud @Inject constructor(
    private val repository: ParametroCrecimientoPediatricoLongitudRepository
) {
    suspend operator fun invoke(grupoEtarioId: Int, genero: String, longitudCm: Double, tipoMedicion: String ): ParametroCrecimientoPediatricoLongitud? {
        return repository.findAllByGrupoEtarioIdAndGeneroAndLongitud(grupoEtarioId, genero, longitudCm, tipoMedicion)
    }
}