package com.nutrizulia.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.data.repository.collection.PacienteRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.nutrizulia.util.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

@HiltWorker
class AppointmentReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val consultaRepository: ConsultaRepository,
    private val pacienteRepository: PacienteRepository,
    private val notificationHelper: NotificationHelper,
    private val sessionManager: SessionManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME_PREFIX = "appointment_reminder_"
        const val KEY_TYPE = "type" // values: "12h" or "1h"
        const val KEY_CONSULTA_ID = "consulta_id" // only for 1h details
        const val KEY_START = "start" // ISO_LOCAL_DATE_TIME string, window start for 12h count
        const val KEY_END = "end" // ISO_LOCAL_DATE_TIME string, window end for 12h count
        private const val TAG = "AppointmentReminderWorker"
    }

    override suspend fun doWork(): Result {
        notificationHelper.ensureChannels()
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()
        return try {
            when (type) {
                "12h" -> handle12hCount()
                "1h" -> handle1hDetails()
                else -> Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en AppointmentReminderWorker", e)
            Result.failure()
        }
    }

    private suspend fun handle12hCount(): Result {
        val startStr = inputData.getString(KEY_START) ?: return Result.failure()
        val endStr = inputData.getString(KEY_END) ?: return Result.failure()
        val start = LocalDateTime.parse(startStr)
        val end = LocalDateTime.parse(endStr)

        // Esperar por el ID de institución (hasta 30s) para evitar silencios cuando aún no está listo
        val usuarioInstitucionId = try {
            withTimeout(30_000) {
                sessionManager.currentInstitutionIdFlow.filterNotNull().first()
            }
        } catch (e: TimeoutCancellationException) {
            return Result.retry()
        }
        val count = consultaRepository.countPendingOrRescheduledBetween(usuarioInstitucionId, start, end)
        notificationHelper.show12hCount(count)
        return Result.success()
    }

    private suspend fun handle1hDetails(): Result {
        val consultaId = inputData.getString(KEY_CONSULTA_ID) ?: return Result.failure()
        val usuarioInstitucionId = try {
            withTimeout(30_000) {
                sessionManager.currentInstitutionIdFlow.filterNotNull().first()
            }
        } catch (e: TimeoutCancellationException) {
            return Result.retry()
        }
        val pacienteConCita = pacienteRepository.findPacienteConCitaByConsultaId(usuarioInstitucionId, consultaId)
            ?: return Result.success()

        // Formatear fecha hora
        val fechaHora = pacienteConCita.fechaHoraProgramadaConsulta?.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale("es", "ES"))
        ) ?: "Sin hora"

        notificationHelper.show1hDetails(
            pacienteNombre = pacienteConCita.nombreCompleto,
            servicioNombre = pacienteConCita.nombreEspecialidadRemitente,
            tipoActividadNombre = pacienteConCita.nombreTipoActividad,
            fechaHora = fechaHora,
            consultaId = consultaId
        )
        return Result.success()
    }
}