package com.nutrizulia.data.repository.catalog.sync

import com.nutrizulia.data.remote.dto.catalog.VersionResponseDto

data class CatalogSyncResult(
    val tableName: String,
    val insertedCount: Int = 0,
    val isSuccess: Boolean = true,
    val message: String = ""
)

interface CatalogSyncer {
    val tableName: String
    suspend fun sync(remoteVersion: VersionResponseDto): CatalogSyncResult
}