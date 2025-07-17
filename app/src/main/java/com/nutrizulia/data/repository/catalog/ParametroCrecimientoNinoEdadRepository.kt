package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.ParametroCrecimientoNinoEdadDao
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoNinoEdad
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class ParametroCrecimientoNinoEdadRepository @Inject constructor(
    private val dao: ParametroCrecimientoNinoEdadDao
) {
    suspend fun findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndEdadMes(tipoIndicadorId: Int, grupoEtarioId: Int, genero: String, edadMes: Int): ParametroCrecimientoNinoEdad? {
        return dao.findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndEdadMes(tipoIndicadorId, grupoEtarioId, genero, edadMes)?.toDomain()
    }
}