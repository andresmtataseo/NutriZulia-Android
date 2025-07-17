package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParametroCrecimientoPediatricoLongitudRepository
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoPediatricoLongitud
import javax.inject.Inject

class GetParametroCrecimientoPediatricoLongitud @Inject constructor(
    private val repository: ParametroCrecimientoPediatricoLongitudRepository
) {
    suspend operator fun invoke(tipoIndicadorId: Int, grupoEtarioId: Int, genero: String, longitudCm: Int, tipoMedicion: String ): ParametroCrecimientoPediatricoLongitud? {
        return repository.findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndLongitud(tipoIndicadorId, grupoEtarioId, genero, longitudCm, tipoMedicion)
    }
}