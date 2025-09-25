package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.EvaluacionAntropometricaDao
import com.nutrizulia.data.local.dao.catalog.TipoIndicadorDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.catalog.TipoIndicador
import com.nutrizulia.domain.model.catalog.toDomain
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class EvaluacionAntropometricaRepository @Inject constructor(
    private val dao: EvaluacionAntropometricaDao,
    private val tipoIndicadorDao: TipoIndicadorDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {

    suspend fun findLatestEvaluacionesByPacienteId(pacienteId: String): Map<TipoIndicador, EvaluacionAntropometrica> {
        // Obtener todos los tipos de indicadores únicos para este paciente
        val tipoIndicadorIds = dao.findDistinctTipoIndicadorIdsByPacienteId(pacienteId)
        
        val result = mutableMapOf<TipoIndicador, EvaluacionAntropometrica>()
        
        // Para cada tipo de indicador, obtener la evaluación más reciente
        for (tipoIndicadorId in tipoIndicadorIds) {
            val evaluacion = dao.findLatestByPacienteIdAndTipoIndicador(pacienteId, tipoIndicadorId)
            val tipoIndicador = tipoIndicadorDao.findById(tipoIndicadorId)
            
            if (evaluacion != null && tipoIndicador != null) {
                result[tipoIndicador.toDomain()] = evaluacion.toDomain()
            }
        }
        
        return result
    }

    suspend fun findLatestEvaluacionByPacienteIdAndTipoIndicador(
        pacienteId: String, 
        tipoIndicadorId: Int
    ): EvaluacionAntropometrica? {
        return dao.findLatestByPacienteIdAndTipoIndicador(pacienteId, tipoIndicadorId)?.toDomain()
    }

    suspend fun upsertAll(evaluacionAntropometrica: List<EvaluacionAntropometrica>) {
        dao.upsertAll(evaluacionAntropometrica.map { it.toEntity() })
    }

    suspend fun insertAll(evaluacionAntropometrica: List<EvaluacionAntropometrica>) {
        dao.insertAll(evaluacionAntropometrica.map { it.toEntity() })
    }

    suspend fun upsert(evaluacionAntropometrica: EvaluacionAntropometrica) {
        dao.upsert(evaluacionAntropometrica.toEntity())
    }

    suspend fun deleteByConsultaId(consultaId: String): Int {
        return dao.deleteByConsultaId(consultaId)
    }

    suspend fun findAllByConsultaId(idConsulta: String): List<EvaluacionAntropometrica> {
        return dao.findAllByConsultaId(idConsulta).map { it.toDomain() }
    }

    suspend fun findAllNotSynced(usuarioInstitucionId: Int): Int {
        return dao.countNotSynced(usuarioInstitucionId)
    }

    suspend fun sincronizarEvaluacionesAntropometricasBatch(usuarioInstitucionId: Int): SyncResult<BatchSyncResult> {
        return try {
            val evaluacionesPendientes = dao.findAllNotSynced(usuarioInstitucionId)
            if (evaluacionesPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay evaluaciones antropométricas para sincronizar"
                )
            }

            val evaluacionesDto = evaluacionesPendientes.map { it.toDto() }
            val response = batchApi.syncEvaluacionesAntropometricasBatch(evaluacionesDto)

            response.toBatchSyncResult { batchResult ->
                batchResult.successfulUuids.forEach { uuid ->
                    dao.markAsSynced(uuid, LocalDateTime.now())
                }

                if (batchResult.failedUuids.isNotEmpty()) {
                    if (batchResult.successfulUuids.isNotEmpty()) {
                        SyncResult.Success(
                            batchResult,
                            "Sincronización parcial: ${batchResult.successfulUuids.size} exitosos, ${batchResult.failedUuids.size} fallidos"
                        )
                    } else {
                        SyncResult.BusinessError(
                            409,
                            response.body()?.message ?: "Error en la sincronización de evaluaciones antropométricas",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de evaluaciones antropométricas completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincronización completa de evaluaciones antropométricas desde el backend
     * Recupera todas las evaluaciones del usuario y las guarda localmente
     * @param usuarioInstitucionId ID de la institución del usuario
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncEvaluacionesAntropometricas(): SyncResult<Int> {
        return try {
            android.util.Log.d("EvaluacionAntropometricaRepository", "Iniciando sincronización completa de evaluaciones antropométricas")
            
            val response = fullSyncApi.getFullSyncEvaluacionesAntropometricas()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("EvaluacionAntropometricaRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} evaluaciones antropométricas")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    dao.upsertAll(entidades)
                    
                    android.util.Log.d("EvaluacionAntropometricaRepository", "Sincronización completa de evaluaciones antropométricas exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de evaluaciones antropométricas exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("EvaluacionAntropometricaRepository", "No hay evaluaciones antropométricas para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay evaluaciones antropométricas para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("EvaluacionAntropometricaRepository", "Error en sincronización completa de evaluaciones antropométricas", e)
            e.toSyncResult()
        }
    }

}