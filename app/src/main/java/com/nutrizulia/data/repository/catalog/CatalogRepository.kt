package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.remote.api.catalog.CatalogService
import com.nutrizulia.data.remote.dto.catalog.VersionResponseDto
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
                    CatalogSyncResult("all_catalogs", isSuccess = false, message = "Error fetching remote versions.")
                )
            }

            val remoteVersions = versionResponse.body().orEmpty()
            val allResults = mutableListOf<CatalogSyncResult>()

            // --- Lotes de Sincronización con Orden de Dependencia Corregido ---

            // Lote 1: Catálogos verdaderamente independientes.
            val batch1_Independents = setOf(
                "roles", "etnias", "enfermedades", "nacionalidades", "especialidades",
                "grupos_etarios", "parentescos", "regex", "riesgos_biologicos",
                "tipos_actividades", "tipos_indicadores", "tipos_instituciones"
            )

            // Lote 2: Catálogos de Localización (Nivel 1).
            val batch2_Locations_L1 = setOf("estados")

            // Lote 3: Catálogos de Localización (Nivel 2) - Dependen de 'estados'.
            val batch3_Locations_L2 = setOf("municipios", "municipios_sanitarios")

            // Lote 4: Catálogos de Localización (Nivel 3) - Dependen de 'municipios'.
            val batch4_Locations_L3 = setOf("parroquias")

            // Lote 5: Catálogos que dependen de los lotes anteriores.
            // 'instituciones' se mueve aquí porque depende de 'tipos_instituciones' (Lote 1) y 'parroquias' (Lote 4).
            val batch5_Dependents = setOf(
                "instituciones", "parametros_crecimientos_ninos_edad",
                "parametros_crecimientos_pediatricos_edad", "parametros_crecimientos_pediatricos_longitud",
                "reglas_interpretaciones_imc", "reglas_interpretaciones_percentil",
                "reglas_interpretaciones_z_score"
            )

            // --- Ejecución Secuencial de Lotes ---
            allResults.addAll(syncBatch(remoteVersions.filter { it.nombreTabla in batch1_Independents }))
            allResults.addAll(syncBatch(remoteVersions.filter { it.nombreTabla in batch2_Locations_L1 }))
            allResults.addAll(syncBatch(remoteVersions.filter { it.nombreTabla in batch3_Locations_L2 }))
            allResults.addAll(syncBatch(remoteVersions.filter { it.nombreTabla in batch4_Locations_L3 }))
            allResults.addAll(syncBatch(remoteVersions.filter { it.nombreTabla in batch5_Dependents }))

            // Sincronizar cualquier tabla restante que no esté en los lotes.
            val allBatchedTables = batch1_Independents + batch2_Locations_L1 + batch3_Locations_L2 + batch4_Locations_L3 + batch5_Dependents
            val remainingTables = remoteVersions.filter { it.nombreTabla !in allBatchedTables }
            if (remainingTables.isNotEmpty()) {
                allResults.addAll(syncBatch(remainingTables))
            }

            return@withContext allResults

        } catch (err: Exception) {
            return@withContext listOf(
                CatalogSyncResult("all_catalogs", isSuccess = false, message = "An unexpected error occurred: ${err.message}")
            )
        }
    }

    private suspend fun syncBatch(versionsToSync: List<VersionResponseDto>): List<CatalogSyncResult> {
        if (versionsToSync.isEmpty()) return emptyList()

        return withContext(Dispatchers.IO) {
            val syncJobs = versionsToSync.map { version ->
                async {
                    syncers[version.nombreTabla]?.sync(version)
                        ?: CatalogSyncResult(version.nombreTabla, isSuccess = false, message = "No syncer found for this table.")
                }
            }
            syncJobs.awaitAll()
        }
    }
}