package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.ParametroCrecimientoPediatricoEdadDao
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoPediatricoEdad
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class ParametroCrecimientoPediatricoEdadRepository @Inject constructor(
    private val dao: ParametroCrecimientoPediatricoEdadDao
) {
    suspend fun findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndEdadMes(tipoIndicadorId: Int, grupoEtarioId: Int, genero: String, edadDia: Int): ParametroCrecimientoPediatricoEdad? {
        return dao.findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndEdadMes(tipoIndicadorId, grupoEtarioId, genero, edadDia)?.toDomain()
    }
}