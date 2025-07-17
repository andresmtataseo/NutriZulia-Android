package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParametroCrecimientoNinoEdadRepository
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoNinoEdad
import javax.inject.Inject

class GetParametroCrecimientoNinoEdad @Inject constructor(
    private val repository: ParametroCrecimientoNinoEdadRepository
){
    suspend operator fun invoke(tipoIndicadorId: Int, grupoEtarioId: Int, genero: String, edadMes: Int) : ParametroCrecimientoNinoEdad? {
        return repository.findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndEdadMes(tipoIndicadorId, grupoEtarioId, genero, edadMes)
    }
}