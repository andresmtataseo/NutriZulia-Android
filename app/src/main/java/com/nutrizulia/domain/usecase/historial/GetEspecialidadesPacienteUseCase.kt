package com.nutrizulia.domain.usecase.historial

import com.nutrizulia.data.local.dao.HistorialMedicoDao
import javax.inject.Inject

class GetEspecialidadesPacienteUseCase @Inject constructor(
    private val historialMedicoDao: HistorialMedicoDao
) {
    suspend operator fun invoke(pacienteId: String): List<String> {
        return historialMedicoDao.getEspecialidadesDelPaciente(pacienteId)
    }
}