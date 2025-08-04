package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleVitalDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.domain.model.collection.DetalleVital
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class DetalleVitalRepository @Inject constructor(
    private val dao: DetalleVitalDao,
    private val api: ICollectionSyncService
) {
    suspend fun upsert(detalleVital: DetalleVital) {
        dao.upsert(detalleVital.toEntity())
    }

    suspend fun findByConsultaId(consultaId: String) : DetalleVital? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }
}