package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParametroCrecimientoPediatricoEdadRepository
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoPediatricoEdad
import javax.inject.Inject

class GetParametroCrecimientoPediatricoEdad @Inject constructor(
    private val repository: ParametroCrecimientoPediatricoEdadRepository
) {
    suspend operator fun invoke(tipoIndicadorId: Int, grupoEtarioId: Int, genero: String, edadDia: Int): ParametroCrecimientoPediatricoEdad? {
        return repository.findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndEdadMes(tipoIndicadorId, grupoEtarioId, genero, edadDia)
    }
}