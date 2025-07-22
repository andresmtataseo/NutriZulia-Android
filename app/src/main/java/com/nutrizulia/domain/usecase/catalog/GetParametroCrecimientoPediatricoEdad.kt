package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParametroCrecimientoPediatricoEdadRepository
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoPediatricoEdad
import javax.inject.Inject

class GetParametroCrecimientoPediatricoEdad @Inject constructor(
    private val repository: ParametroCrecimientoPediatricoEdadRepository
) {
    suspend operator fun invoke(grupoEtarioId: Int, genero: String, edadDia: Int): List<ParametroCrecimientoPediatricoEdad> {
        return repository.findAllByGrupoEtarioIdAndGeneroAndEdadMes(grupoEtarioId, genero, edadDia)
    }
}