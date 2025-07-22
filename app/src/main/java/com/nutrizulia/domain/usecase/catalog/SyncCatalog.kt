package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.CatalogRepository
import com.nutrizulia.data.repository.catalog.sync.CatalogSyncResult
import javax.inject.Inject

/**
 * Executes the synchronization of all catalogs.
 * It processes the detailed results from the repository and returns a simple,
 * consolidated result indicating overall success or failure.
 */
class SyncCatalog @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(): SyncCatalogsResult {
        val results: List<CatalogSyncResult> = catalogRepository.syncAllCatalogs()

        // Find any syncs that were not successful.
        val failedSyncs: List<CatalogSyncResult> = results.filter { !it.isSuccess }

        return if (failedSyncs.isEmpty()) {
            SyncCatalogsResult.Success
        } else {
            // Build a user-friendly error message.
            val failedTables: String = failedSyncs.joinToString(", ") { it.tableName }
            val errorMessage = "Fallo la sincronización de: $failedTables."

            // Return the failure result with the summary message and detailed logs.
            SyncCatalogsResult.Failure(errorMessage, failedSyncs)
        }
    }
}

/**
 * Represents the consolidated result of the entire catalog synchronization process.
 */
sealed class SyncCatalogsResult {
    object Success : SyncCatalogsResult()
    data class Failure(
        val message: String,
        val details: List<CatalogSyncResult>
    ) : SyncCatalogsResult()
}