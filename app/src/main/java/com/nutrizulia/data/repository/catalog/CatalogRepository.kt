package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.remote.api.catalog.CatalogService
import com.nutrizulia.data.repository.catalog.sync.CatalogSyncResult
import com.nutrizulia.data.repository.catalog.sync.CatalogSyncer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CatalogRepository @Inject constructor(
    private val catalogService: CatalogService,
    private val syncers: Map<String, @JvmSuppressWildcards CatalogSyncer>
) {

    suspend fun syncAllCatalogs(): List<CatalogSyncResult> = withContext(Dispatchers.IO) {
        try {
            val versionResponse = catalogService.getVersion()
            if (!versionResponse.isSuccessful || versionResponse.body() == null) {
                return@withContext listOf(
                    CatalogSyncResult(
                        "all_catalogs",
                        isSuccess = false,
                        message = "Error fetching remote versions from backend."
                    )
                )
            }

            val remoteVersions = versionResponse.body().orEmpty()

            val syncJobs = remoteVersions.map { version ->
                async {
                    syncers[version.nombreTabla]?.sync(version)
                        ?: CatalogSyncResult(
                            tableName = version.nombreTabla,
                            isSuccess = false,
                            message = "No syncer found for this table."
                        )
                }
            }

            return@withContext syncJobs.awaitAll()

        } catch (err: Exception) {
            return@withContext listOf(
                CatalogSyncResult(
                    "all_catalogs",
                    isSuccess = false,
                    message = "An unexpected error occurred: ${err.message}"
                )
            )
        }
    }
}