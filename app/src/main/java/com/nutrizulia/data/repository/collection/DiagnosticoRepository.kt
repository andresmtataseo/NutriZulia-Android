package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DiagnosticoDao
import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import javax.inject.Inject

class DiagnosticoRepository @Inject constructor(
    private val dao: DiagnosticoDao,
    private val api: ICollectionSyncService
) {
    suspend fun insertAll(diagnosticos: List<DiagnosticoEntity>): List<Long> = dao.insertAll(diagnosticos)
    suspend fun deleteByConsultaId(consultaId: String): Int = dao.deleteByConsultaId(consultaId)
    suspend fun findByConsultaId(consultaId: String): List<DiagnosticoEntity> = dao.findByConsultaId(consultaId)
} 