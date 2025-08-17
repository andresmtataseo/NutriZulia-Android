package com.nutrizulia.domain.usecase.historial

import com.nutrizulia.data.local.dao.HistorialMedicoDao
import com.nutrizulia.data.local.view.HistorialMedicoCompletoView
import javax.inject.Inject

class GetHistorialPaginadoUseCase @Inject constructor(
    private val historialMedicoDao: HistorialMedicoDao
) {
    suspend operator fun invoke(
        pacienteId: String,
        pagina: Int,
        tamanoPagina: Int = 20
    ): List<HistorialMedicoCompletoView> {
        val offset = pagina * tamanoPagina
        return historialMedicoDao.getHistorialPacientePaginado(
            pacienteId = pacienteId,
            limit = tamanoPagina,
            offset = offset
        )
    }
}