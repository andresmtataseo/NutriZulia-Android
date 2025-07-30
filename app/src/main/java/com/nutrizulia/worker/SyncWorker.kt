package com.nutrizulia.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nutrizulia.data.repository.sync.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker para ejecutar la sincronización de datos en segundo plano.
 * 
 * Utiliza WorkManager para programar y ejecutar la sincronización de forma periódica
 * y eficiente, respetando las restricciones del sistema (batería, red, etc.).
 * 
 * La sincronización se ejecuta en el contexto de IO para operaciones de red y base de datos.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "sync_work"
        const val TAG_SYNC = "sync"
    }

    /**
     * Ejecuta la sincronización de datos.
     * 
     * @return Result.success() si la sincronización fue exitosa,
     *         Result.failure() si ocurrió algún error
     */
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            // Ejecutar la sincronización
            val syncResult = syncRepository.synchronize()
            
            if (syncResult.isSuccess) {
                // La sincronización fue exitosa
                Result.success()
            } else {
                // La sincronización falló
                Result.failure()
            }
        } catch (exception: Exception) {
            // Manejar cualquier excepción no capturada
            Result.failure()
        }
    }
}