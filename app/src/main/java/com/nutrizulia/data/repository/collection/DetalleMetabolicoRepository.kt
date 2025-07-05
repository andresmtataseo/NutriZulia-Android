package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleMetabolicoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import javax.inject.Inject

class DetalleMetabolicoRepository @Inject constructor(
    private val dao: DetalleMetabolicoDao
) {
    suspend fun upsert(detalleMetabolico: DetalleMetabolico) {
        dao.upsert(detalleMetabolico.toEntity())
    }
}