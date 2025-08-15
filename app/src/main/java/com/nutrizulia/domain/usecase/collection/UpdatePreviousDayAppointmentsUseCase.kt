package com.nutrizulia.domain.usecase.collection

import android.util.Log
import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.domain.model.collection.Consulta
import javax.inject.Inject

class UpdatePreviousDayAppointmentsUseCase @Inject constructor(
    private val consultaRepository: ConsultaRepository
) {
    suspend operator fun invoke(): UpdatePreviousDayAppointmentsResult {
        return try {
            Log.d("UpdatePreviousDayAppointments", "Starting update of previous day appointments")
            
            // First, get the appointments that will be updated
            val appointmentsToUpdate: List<Consulta> = consultaRepository.findPreviousDayPendingAppointments()
            
            if (appointmentsToUpdate.isEmpty()) {
                Log.d("UpdatePreviousDayAppointments", "No appointments found to update")
                return UpdatePreviousDayAppointmentsResult.Success(
                    updatedCount = 0,
                    message = "No appointments found to update"
                )
            }
            
            Log.d("UpdatePreviousDayAppointments", "Found ${appointmentsToUpdate.size} appointments to update")
            
            // Update the appointments to NO_ASISTIO
            val updatedCount: Int = consultaRepository.updatePreviousDayPendingAppointmentsToNoShow()
            
            Log.d("UpdatePreviousDayAppointments", "Successfully updated $updatedCount appointments to NO_ASISTIO")
            
            UpdatePreviousDayAppointmentsResult.Success(
                updatedCount = updatedCount,
                message = "Successfully updated $updatedCount appointments to NO_ASISTIO"
            )
        } catch (exception: Exception) {
            Log.e("UpdatePreviousDayAppointments", "Error updating previous day appointments", exception)
            UpdatePreviousDayAppointmentsResult.Error(
                exception = exception,
                message = "Error updating previous day appointments: ${exception.message}"
            )
        }
    }
}

sealed class UpdatePreviousDayAppointmentsResult {
    data class Success(
        val updatedCount: Int,
        val message: String
    ) : UpdatePreviousDayAppointmentsResult()
    
    data class Error(
        val exception: Exception,
        val message: String
    ) : UpdatePreviousDayAppointmentsResult()
}