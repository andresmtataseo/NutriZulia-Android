package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.ParametroCrecimientoPediatricoLongitudDao
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoPediatricoLongitud
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject

class ParametroCrecimientoPediatricoLongitudRepository @Inject constructor(
    private val dao: ParametroCrecimientoPediatricoLongitudDao
) {
    suspend fun findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndLongitud(
        tipoIndicadorId: Int,
        grupoEtarioId: Int,
        genero: String,
        longitudCm: Int,
        tipoMedicion: String
    ): ParametroCrecimientoPediatricoLongitud? {
        return dao.findByTipoIndicadorIdAndGrupoEtarioIdAndGeneroAndLongitudCmAndTipoMedicion(
            tipoIndicadorId,
            grupoEtarioId,
            genero,
            longitudCm,
            tipoMedicion
        )?.toDomain()
    }
}