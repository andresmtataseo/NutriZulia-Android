package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.EvaluacionAntropometricaDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.EvaluacionAntropometricaDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.domain.model.toSyncResult
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
    
    suspend fun sincronizarEvaluacionesAntropometricas(): SyncResult<List<EvaluacionAntropometricaDto>> {
        return try {
            val evaluacionesPendientes = dao.findAllNotSynced()
            if (evaluacionesPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay evaluaciones antropométricas para sincronizar")
            }
            val evaluacionesDto = evaluacionesPendientes.map { it.toDto() }
            val response = api.syncEvaluacionesAntropometricas(evaluacionesDto)
            
            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true
                    )
                    dao.upsert(entity)
                }
                val cantidadSincronizados = data.size
                val mensaje = "Sincronizadas $cantidadSincronizados evaluaciones antropométricas"
                SyncResult.Success(data, mensaje)
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

}