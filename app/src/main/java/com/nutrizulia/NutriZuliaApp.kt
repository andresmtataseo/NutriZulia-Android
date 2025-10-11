package com.nutrizulia

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nutrizulia.util.UpdatePreviousDayAppointmentsWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import java.time.Duration
import java.time.ZonedDateTime

@HiltAndroidApp
class NutriZuliaApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: androidx.hilt.work.HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        scheduleUpdatePreviousDayAppointmentsWork()
        // Ejecutar inmediatamente al iniciar la app para actualizar citas del día anterior
        triggerImmediateUpdatePreviousDayAppointmentsWork()
    }

    private fun scheduleUpdatePreviousDayAppointmentsWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .setRequiresStorageNotLow(false)
            .build()

        val now = ZonedDateTime.now()
        val nextRun = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        val initialDelayMillis = Duration.between(now, nextRun).toMillis()

        val workRequest = PeriodicWorkRequestBuilder<UpdatePreviousDayAppointmentsWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UpdatePreviousDayAppointmentsWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    // Ejecuta una vez al iniciar la aplicación para asegurar la actualización inmediata
    private fun triggerImmediateUpdatePreviousDayAppointmentsWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .setRequiresStorageNotLow(false)
            .build()

        val oneTimeRequest = OneTimeWorkRequestBuilder<UpdatePreviousDayAppointmentsWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            UpdatePreviousDayAppointmentsWorker.WORK_NAME + "_startup",
            ExistingWorkPolicy.KEEP,
            oneTimeRequest
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}