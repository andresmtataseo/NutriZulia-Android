package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.ParametroCrecimientoPediatricoLongitudDao
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoPediatricoLongitud
import com.nutrizulia.domain.model.catalog.toDomain
import javax.inject.Inject
import kotlin.math.abs

class ParametroCrecimientoPediatricoLongitudRepository @Inject constructor(
    private val dao: ParametroCrecimientoPediatricoLongitudDao
) {
    suspend fun findAllByGrupoEtarioIdAndGeneroAndLongitud(
        grupoEtarioId: Int,
        genero: String,
        longitudCm: Double,
        tipoMedicion: String
    ): ParametroCrecimientoPediatricoLongitud? {
        val delta = 0.11  // un poco más de 0.1 para asegurar coincidencia
        val parametros = dao.findAllByGrupoEtarioIdAndGeneroAndLongitudCmAndTipoMedicion(
            grupoEtarioId = grupoEtarioId,
            genero = genero,
            minLongitud = longitudCm - delta,
            maxLongitud = longitudCm + delta,
            tipoMedicion = tipoMedicion
        )
        return parametros.minByOrNull { abs(it.longitudCm - longitudCm) }?.toDomain()
    }

}