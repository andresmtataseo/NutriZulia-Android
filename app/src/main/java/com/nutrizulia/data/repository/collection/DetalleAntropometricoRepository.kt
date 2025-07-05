package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleAntropometricoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import javax.inject.Inject

class DetalleAntropometricoRepository @Inject constructor(
    private val dao: DetalleAntropometricoDao
) {
    suspend fun upsert(detalleAntropometrico: DetalleAntropometrico) {
        dao.upsert(detalleAntropometrico.toEntity())
    }
}