package com.nutrizulia.domain.usecase.dashboard

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.util.AppointmentReminderWorker
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ScheduleAppointmentRemindersUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val consultaRepository: ConsultaRepository,
    private val sessionManager: SessionManager
) {

    suspend operator fun invoke(): Int {
        val usuarioInstitucionId = sessionManager.currentInstitutionIdFlow.first() ?: return 0
        val now = LocalDateTime.now()

        val workManager = WorkManager.getInstance(context)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .setRequiresStorageNotLow(false)
            .build()

        var scheduledCount = 0

        // 1) Recordatorio de 12h (conteo para la próxima ventana de 12 horas)
        val start = now
        val end = now.plusHours(12)
        val input12h = Data.Builder()
            .putString(AppointmentReminderWorker.KEY_TYPE, "12h")
            .putString(AppointmentReminderWorker.KEY_START, start.toString())
            .putString(AppointmentReminderWorker.KEY_END, end.toString())
            .build()

        val request12h = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
            .setConstraints(constraints)
            .setInputData(input12h)
            .build()

        val uniqueName12h = AppointmentReminderWorker.WORK_NAME_PREFIX + "12h_" + start.toLocalDate().toString()
        workManager.enqueueUniqueWork(uniqueName12h, ExistingWorkPolicy.REPLACE, request12h)
        scheduledCount++

        // 2) Recordatorios de 1h por cada cita próxima pendiente/reprogramada
        val upcoming = consultaRepository.findUpcomingPendingOrRescheduled(usuarioInstitucionId, now)
        upcoming.forEach { consulta ->
            val scheduledDateTime = consulta.fechaHoraProgramada ?: return@forEach
            val triggerTime = scheduledDateTime.minusHours(1)
            val delayMillis = Duration.between(now, triggerTime).toMillis()
            if (delayMillis <= 0) return@forEach

            val input1h = Data.Builder()
                .putString(AppointmentReminderWorker.KEY_TYPE, "1h")
                .putString(AppointmentReminderWorker.KEY_CONSULTA_ID, consulta.id)
                .build()

            val request1h = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                .setConstraints(constraints)
                .setInputData(input1h)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .build()

            val uniqueName1h = AppointmentReminderWorker.WORK_NAME_PREFIX + "1h_" + consulta.id
            workManager.enqueueUniqueWork(uniqueName1h, ExistingWorkPolicy.REPLACE, request1h)
            scheduledCount++
        }

        return scheduledCount
    }
}