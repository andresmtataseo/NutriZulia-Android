package com.nutrizulia.data.repository.catalog.sync

import androidx.room.withTransaction
import com.nutrizulia.data.local.AppDatabase
import com.nutrizulia.data.local.dao.catalog.VersionDao
import com.nutrizulia.data.local.entity.catalog.VersionEntity
import com.nutrizulia.data.remote.dto.catalog.VersionResponseDto

class BaseCatalogSyncer<DTO, ENTITY>(
    override val tableName: String,
    private val database: AppDatabase,
    private val versionDao: VersionDao,
    private val fetchRemoteData: suspend () -> List<DTO>,
    private val mapToEntity: (DTO) -> ENTITY,
    private val syncOperation: suspend (List<ENTITY>) -> Unit,
    private val validateSyncSuccess: suspend (List<ENTITY>) -> Boolean = { true }
) : CatalogSyncer {

    override suspend fun sync(remoteVersion: VersionResponseDto): CatalogSyncResult {
        return try {
            val localVersion: Int = versionDao.findByNombre(tableName)?.version ?: 0

            if (localVersion >= remoteVersion.version) {
                return CatalogSyncResult(tableName, isSuccess = true, message = "Already up to date.")
            }

            val remoteList: List<DTO> = fetchRemoteData()
            if (remoteList.isEmpty()) {
                // Si la lista remota está vacía, actualizamos la versión dentro de una transacción
                database.withTransaction {
                    updateLocalVersion(remoteVersion)
                }
                return CatalogSyncResult(tableName, isSuccess = true, message = "Remote list is empty, version updated.")
            }

            val entities: List<ENTITY> = remoteList.map(mapToEntity)
            var insertedCount = 0

            // ✅ Usar transacción atómica para toda la operación de sincronización
            database.withTransaction {
                // Ejecutar la operación de sincronización
                syncOperation(entities)
                
                // Validar que la sincronización fue exitosa
                if (!validateSyncSuccess(entities)) {
                    throw Exception("La validación de sincronización falló para la tabla $tableName")
                }
                
                // Solo actualizar la versión si todo fue exitoso
                updateLocalVersion(remoteVersion)
                insertedCount = entities.size
            }

            CatalogSyncResult(
                tableName = tableName,
                insertedCount = insertedCount,
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