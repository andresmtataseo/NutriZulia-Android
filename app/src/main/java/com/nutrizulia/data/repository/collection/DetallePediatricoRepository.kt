package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetallePediatricoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.domain.model.collection.DetallePediatrico
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class DetallePediatricoRepository @Inject constructor(
    private val dao: DetallePediatricoDao,
    private val api: ICollectionSyncService
) {
    suspend fun upsert(detallePediatrico: DetallePediatrico) {
        dao.upsert(detallePediatrico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String) : DetallePediatrico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }
}