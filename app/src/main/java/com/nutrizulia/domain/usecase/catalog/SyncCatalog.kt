package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.CatalogRepository
import com.nutrizulia.util.SyncResult
import javax.inject.Inject

class SyncCatalog @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(): SyncResult {
        return catalogRepository.syncAllCatalogs()
    }
}