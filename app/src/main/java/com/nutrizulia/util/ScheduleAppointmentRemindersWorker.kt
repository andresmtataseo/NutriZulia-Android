package com.nutrizulia.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nutrizulia.domain.usecase.dashboard.ScheduleAppointmentRemindersUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ScheduleAppointmentRemindersWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val scheduleAppointmentRemindersUseCase: ScheduleAppointmentRemindersUseCase
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "schedule_appointment_reminders_daily"
        private const val TAG = "ScheduleAppointmentRemindersWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val count = scheduleAppointmentRemindersUseCase()
            Log.d(TAG, "Recordatorios encolados: $count")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error programando recordatorios", e)
            Result.failure()
        }
    }
}