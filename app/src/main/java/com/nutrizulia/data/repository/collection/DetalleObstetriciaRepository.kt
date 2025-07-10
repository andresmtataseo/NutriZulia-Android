package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleObstetriciaDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class DetalleObstetriciaRepository @Inject constructor(
    private val dao: DetalleObstetriciaDao
) {
    suspend fun upsert(it: DetalleObstetricia) {
        dao.upsert(it.toEntity())
    }
    suspend fun findByConsultaId(ConsultaId: String): DetalleObstetricia? {
        return dao.findByConsultaId(ConsultaId)?.toDomain()
    }
}