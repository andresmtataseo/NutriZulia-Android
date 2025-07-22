package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.ParametroCrecimientoNinoEdadRepository
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoNinoEdad
import javax.inject.Inject

class GetParametroCrecimientoNinoEdad @Inject constructor(
    private val repository: ParametroCrecimientoNinoEdadRepository
){
    suspend operator fun invoke(grupoEtarioId: Int, genero: String, edadMes: Int) : List<ParametroCrecimientoNinoEdad> {
        return repository.findAllByGrupoEtarioIdAndGeneroAndEdadMes(grupoEtarioId, genero, edadMes)
    }
}