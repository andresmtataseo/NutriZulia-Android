package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.EvaluacionAntropometricaDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class EvaluacionAntropometricaRepository @Inject constructor(
    private val dao: EvaluacionAntropometricaDao,
    private val api: ICollectionSyncService
) {

    suspend fun upsertAll(evaluacionAntropometrica: List<EvaluacionAntropometrica>) {
        dao.upsertAll(evaluacionAntropometrica.map { it.toEntity() })
    }

    suspend fun findAllByConsultaId(idConsulta: String): List<EvaluacionAntropometrica> {
        return dao.findAllByConsultaId(idConsulta).map { it.toDomain() }
    }

}