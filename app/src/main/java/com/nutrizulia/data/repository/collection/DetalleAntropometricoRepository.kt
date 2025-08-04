package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleAntropometricoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class DetalleAntropometricoRepository @Inject constructor(
    private val dao: DetalleAntropometricoDao,
    private val api: ICollectionSyncService
) {
    suspend fun upsert(detalleAntropometrico: DetalleAntropometrico) {
        dao.upsert(detalleAntropometrico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String): DetalleAntropometrico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }
}