package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleMetabolicoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class DetalleMetabolicoRepository @Inject constructor(
    private val dao: DetalleMetabolicoDao,
    private val api: ICollectionSyncService
) {
    suspend fun upsert(detalleMetabolico: DetalleMetabolico) {
        dao.upsert(detalleMetabolico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String) : DetalleMetabolico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }
}