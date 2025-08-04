package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.RepresentanteDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.RepresentanteDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toSyncResult
import javax.inject.Inject

class RepresentanteRepository @Inject constructor(
    private val dao: RepresentanteDao,
    private val api: ICollectionSyncService
) {
    suspend fun findAll(idUsuarioInstitucion: Int): List<Representante> {
        return dao.findAll(idUsuarioInstitucion).map { it.toDomain() }
    }

    suspend fun findByFiltro(idUsuarioInstitucion: Int, filtro: String): List<Representante> {
        return dao.findAllByUsuarioInstitucionIdAndFilter(idUsuarioInstitucion, filtro).map { it.toDomain() }
    }

    suspend fun upsert(representante: Representante) {
        dao.upsert(representante.toEntity())
    }

    suspend fun findByCedula(usuarioInstitucionId: Int, cedula: String): Representante? {
        return dao.findByCedula(usuarioInstitucionId, cedula)?.toDomain()
    }

    suspend fun findById(usuarioInstitucionId: Int, representanteId: String): Representante? {
        return dao.findById(usuarioInstitucionId, representanteId)?.toDomain()
    }

    suspend fun sincronizarRepresentantes(): SyncResult<List<RepresentanteDto>> {
        return try {
            val representantesPendientes = dao.findAllNotSynced()
            if (representantesPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay representantes para sincronizar")
            }
            val representantesDto = representantesPendientes.map { it.toDto() }
            val response = api.syncRepresentantes(representantesDto)

            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true
                    )
                    dao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de representantes completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }
}