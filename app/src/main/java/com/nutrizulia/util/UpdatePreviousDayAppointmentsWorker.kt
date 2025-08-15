package com.nutrizulia.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nutrizulia.domain.usecase.collection.UpdatePreviousDayAppointmentsUseCase
import com.nutrizulia.domain.usecase.collection.UpdatePreviousDayAppointmentsResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdatePreviousDayAppointmentsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val updatePreviousDayAppointmentsUseCase: UpdatePreviousDayAppointmentsUseCase
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "update_previous_day_appointments_work"
        private const val TAG = "UpdatePreviousDayAppointmentsWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting UpdatePreviousDayAppointmentsWorker")
        
        return try {
            when (val result = updatePreviousDayAppointmentsUseCase()) {
                is UpdatePreviousDayAppointmentsResult.Success -> {
                    Log.d(TAG, "Worker completed successfully: ${result.message}")
                    Log.d(TAG, "Updated ${result.updatedCount} appointments")
                    Result.success()
                }
                is UpdatePreviousDayAppointmentsResult.Error -> {
                    Log.e(TAG, "Worker failed: ${result.message}", result.exception)
                    // Return retry for transient errors, failure for permanent errors
                    if (isRetryableError(result.exception)) {
                        Log.d(TAG, "Retrying due to transient error")
                        Result.retry()
                    } else {
                        Log.d(TAG, "Failing due to permanent error")
                        Result.failure()
                    }
                }
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected error in worker", exception)
            if (isRetryableError(exception)) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun isRetryableError(exception: Exception): Boolean {
        return when (exception) {
            is java.net.SocketTimeoutException,
            is java.net.UnknownHostException,
            is java.io.IOException -> true
            else -> false
        }
    }
}