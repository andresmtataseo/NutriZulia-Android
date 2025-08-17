package com.nutrizulia.domain.usecase.historial

import com.nutrizulia.data.local.dao.HistorialMedicoDao
import javax.inject.Inject

class GetAniosHistorialPacienteUseCase @Inject constructor(
    private val historialMedicoDao: HistorialMedicoDao
) {
    suspend operator fun invoke(pacienteId: String): List<Int> {
        return historialMedicoDao.getAniosDelHistorialPaciente(pacienteId)
    }
}