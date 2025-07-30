package com.nutrizulia.util

import android.content.Context
import androidx.work.*
import com.nutrizulia.worker.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Programador de sincronización que gestiona la ejecución periódica
 * de la sincronización de datos usando WorkManager.
 * 
 * Permite programar, cancelar y verificar el estado de la sincronización.
 */
@Singleton
class SyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val workManager = WorkManager.getInstance(context)

    /**
     * Programa la sincronización periódica cada 15 minutos.
     * 
     * La sincronización solo se ejecutará cuando:
     * - El dispositivo esté conectado a una red
     * - La batería no esté baja
     * - El dispositivo no esté en modo Doze
     */
    fun schedulePeriodic() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 5,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(SyncWorker.TAG_SYNC)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    /**
     * Ejecuta una sincronización inmediata (one-time).
     * 
     * Útil para sincronizar después de cambios importantes
     * o cuando el usuario solicita una sincronización manual.
     */
    fun syncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .addTag(SyncWorker.TAG_SYNC)
            .build()

        workManager.enqueueUniqueWork(
            "sync_now",
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest
        )
    }

    /**
     * Cancela toda la sincronización programada.
     */
    fun cancelSync() {
        workManager.cancelUniqueWork(SyncWorker.WORK_NAME)
        workManager.cancelAllWorkByTag(SyncWorker.TAG_SYNC)
    }

    /**
     * Obtiene el estado de la sincronización programada.
     * 
     * @return LiveData con la información del trabajo de sincronización
     */
    fun getSyncStatus() = workManager.getWorkInfosForUniqueWorkLiveData(SyncWorker.WORK_NAME)
}