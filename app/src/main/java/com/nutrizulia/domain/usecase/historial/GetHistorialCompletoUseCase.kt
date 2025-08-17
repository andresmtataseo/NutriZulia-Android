package com.nutrizulia.domain.usecase.historial

import com.nutrizulia.data.local.dao.HistorialMedicoDao
import com.nutrizulia.data.local.view.HistorialMedicoCompletoView
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistorialCompletoUseCase @Inject constructor(
    private val historialMedicoDao: HistorialMedicoDao
) {
    operator fun invoke(pacienteId: String): Flow<List<HistorialMedicoCompletoView>> {
        return historialMedicoDao.getHistorialCompletoPaciente(pacienteId)
    }
}