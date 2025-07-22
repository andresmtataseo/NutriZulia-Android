package com.nutrizulia.data.repository.catalog.sync

import com.nutrizulia.data.local.dao.catalog.VersionDao
import com.nutrizulia.data.local.entity.catalog.VersionEntity
import com.nutrizulia.data.remote.dto.catalog.VersionResponseDto

class BaseCatalogSyncer<DTO, ENTITY>(
    override val tableName: String,
    private val versionDao: VersionDao,
    private val fetchRemoteData: suspend () -> List<DTO>,
    private val mapToEntity: (DTO) -> ENTITY,
    private val syncOperation: suspend (List<ENTITY>) -> Unit
) : CatalogSyncer {

    override suspend fun sync(remoteVersion: VersionResponseDto): CatalogSyncResult {
        return try {
            val localVersion: Int = versionDao.findByNombre(tableName)?.version ?: 0

            if (localVersion >= remoteVersion.version) {
                return CatalogSyncResult(tableName, isSuccess = true, message = "Already up to date.")
            }

            val remoteList: List<DTO> = fetchRemoteData()
            if (remoteList.isEmpty()) {
                // Si la lista remota está vacía, no hacemos nada para evitar borrados accidentales.
                updateLocalVersion(remoteVersion)
                return CatalogSyncResult(tableName, isSuccess = true, message = "Remote list is empty, version updated.")
            }

            val entities: List<ENTITY> = remoteList.map(mapToEntity)

            // ✅ Ejecutamos la operación de sincronización (que será el upsert)
            syncOperation(entities)

            updateLocalVersion(remoteVersion)

            CatalogSyncResult(
                tableName = tableName,
                insertedCount = entities.size, // Nota: esto ahora cuenta registros insertados/actualizados
                isSuccess = true,
                message = "Sync successful."
            )
        } catch (err: Exception) {
            CatalogSyncResult(
                tableName = tableName,
                isSuccess = false,
                message = "Error during sync: ${err.message}"
            )
        }
    }

    private suspend fun updateLocalVersion(versionRemote: VersionResponseDto) {
        val versionEntity = VersionEntity(
            nombreTabla = versionRemote.nombreTabla,
            version = versionRemote.version,
            isUpdated = true
        )
        versionDao.insert(versionEntity)
    }
}