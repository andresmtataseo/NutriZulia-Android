package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleVitalDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.collection.DetalleVital
import javax.inject.Inject

class DetalleVitalRepository @Inject constructor(
    private val dao: DetalleVitalDao
) {
    suspend fun upsert(detalleVital: DetalleVital) {
        dao.upsert(detalleVital.toEntity())
    }
}