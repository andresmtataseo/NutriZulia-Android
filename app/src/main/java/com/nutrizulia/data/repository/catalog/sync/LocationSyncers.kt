package com.nutrizulia.data.repository.catalog.sync

import com.nutrizulia.data.local.dao.catalog.*
import com.nutrizulia.data.local.entity.catalog.VersionEntity
import com.nutrizulia.data.remote.api.catalog.CatalogService
import com.nutrizulia.data.remote.dto.catalog.VersionResponseDto
import com.nutrizulia.data.remote.dto.catalog.toEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

private suspend fun updateVersion(versionDao: VersionDao, remoteVersion: VersionResponseDto) {
    val versionEntity = VersionEntity(
        nombreTabla = remoteVersion.nombreTabla,
        version = remoteVersion.version,
        isUpdated = true
    )
    versionDao.insert(versionEntity)
}

// --- Municipio Syncer ---
class MunicipioSyncer @Inject constructor(
    private val service: CatalogService,
    private val versionDao: VersionDao,
    private val estadoDao: EstadoDao,
    private val municipioDao: MunicipioDao
) : CatalogSyncer {
    override val tableName: String = "municipios"

    override suspend fun sync(remoteVersion: VersionResponseDto): CatalogSyncResult {
        return try {
            if ((versionDao.findByNombre(tableName)?.version ?: 0) >= remoteVersion.version) {
                return CatalogSyncResult(tableName, message = "Already up to date.")
            }

            val allEstados = estadoDao.findAll()
            var totalInserted = 0

            // ✅ Ejecutar llamadas en paralelo
            coroutineScope {
                val municipiosDeferred = allEstados.map { estado ->
                    async { service.getMunicipios(estado.id).body()?.data.orEmpty() }
                }

                // Esperar a que todas las llamadas terminen y aplanar la lista de listas
                val allMunicipios = municipiosDeferred.awaitAll().flatten()

                if (allMunicipios.isNotEmpty()) {
                    val entities = allMunicipios.map { it.toEntity() }
                    // ✅ Usar upsertAll para evitar errores de Foreign Key
                    municipioDao.upsertAll(entities)
                    totalInserted = entities.size
                }
            }

            updateVersion(versionDao, remoteVersion)
            CatalogSyncResult(tableName, insertedCount = totalInserted, isSuccess = true)
        } catch (e: Exception) {
            CatalogSyncResult(tableName, isSuccess = false, message = e.message ?: "Unknown error")
        }
    }
}

// --- Municipio Sanitario Syncer (Mismas mejoras) ---
class MunicipioSanitarioSyncer @Inject constructor(
    private val service: CatalogService,
    private val versionDao: VersionDao,
    private val estadoDao: EstadoDao,
    private val municipioSanitarioDao: MunicipioSanitarioDao
) : CatalogSyncer {
    override val tableName: String = "municipios_sanitarios"

    override suspend fun sync(remoteVersion: VersionResponseDto): CatalogSyncResult {
        return try {
            if ((versionDao.findByNombre(tableName)?.version ?: 0) >= remoteVersion.version) {
                return CatalogSyncResult(tableName, message = "Already up to date.")
            }

            val allEstados = estadoDao.findAll()
            var totalInserted = 0

            coroutineScope {
                val municipiosSanitariosDeferred = allEstados.map { estado ->
                    async { service.getMunicipiosSanitarios(estado.id).body()?.data.orEmpty() }
                }
                val allMunicipiosSanitarios = municipiosSanitariosDeferred.awaitAll().flatten()

                if (allMunicipiosSanitarios.isNotEmpty()) {
                    val entities = allMunicipiosSanitarios.map { it.toEntity() }
                    municipioSanitarioDao.upsertAll(entities)
                    totalInserted = entities.size
                }
            }

            updateVersion(versionDao, remoteVersion)
            CatalogSyncResult(tableName, insertedCount = totalInserted, isSuccess = true)
        } catch (e: Exception) {
            CatalogSyncResult(tableName, isSuccess = false, message = e.message ?: "Unknown error")
        }
    }
}

// --- Parroquia Syncer (Mismas mejoras) ---
class ParroquiaSyncer @Inject constructor(
    private val service: CatalogService,
    private val versionDao: VersionDao,
    private val municipioDao: MunicipioDao,
    private val parroquiaDao: ParroquiaDao
) : CatalogSyncer {
    override val tableName: String = "parroquias"

    override suspend fun sync(remoteVersion: VersionResponseDto): CatalogSyncResult {
        return try {
            if ((versionDao.findByNombre(tableName)?.version ?: 0) >= remoteVersion.version) {
                return CatalogSyncResult(tableName, message = "Already up to date.")
            }

            val allMunicipios = municipioDao.findAll()
            var totalInserted = 0

            coroutineScope {
                val parroquiasDeferred = allMunicipios.map { municipio ->
                    async { service.getParroquias(municipio.estadoId, municipio.id).body()?.data.orEmpty() }
                }
                val allParroquias = parroquiasDeferred.awaitAll().flatten()

                if (allParroquias.isNotEmpty()) {
                    val entities = allParroquias.map { it.toEntity() }
                    parroquiaDao.upsertAll(entities)
                    totalInserted = entities.size
                }
            }

            updateVersion(versionDao, remoteVersion)
            CatalogSyncResult(tableName, insertedCount = totalInserted, isSuccess = true)
        } catch (e: Exception) {
            CatalogSyncResult(tableName, isSuccess = false, message = e.message ?: "Unknown error")
        }
    }
}