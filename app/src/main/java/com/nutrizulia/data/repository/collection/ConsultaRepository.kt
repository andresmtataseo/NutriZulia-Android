package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.ConsultaDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.pojo.DailyAppointmentCount
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.toSyncResult

import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class ConsultaRepository @Inject constructor(
    private val consultaDao: ConsultaDao,
    private val batchApi: IBatchSyncService
) {
    suspend fun upsert(consulta: Consulta): Long {
        return consultaDao.upsert(consulta.toEntity())
    }
    suspend fun getAppointmentCountsByDay(usuarioInstitucionId: Int): Map<LocalDate, Int> {
        val dailyCounts: List<DailyAppointmentCount> = consultaDao.getAppointmentCountsByDay(usuarioInstitucionId)
        return dailyCounts.associate { dailyCount ->
            dailyCount.date to dailyCount.count
        }
    }
    suspend fun countConsultaByPacienteId(pacienteId: String): Boolean {
        val consultationCount: Int = consultaDao.countConsultaByPacienteId(pacienteId)
        return consultationCount > 0
    }
    suspend fun findConsultaProgramadaByPacienteId(idPaciente: String): Consulta? {
        return consultaDao.findConsultaProgramadaByPacienteId(idPaciente)?.toDomain()
    }
    suspend fun findConsultaProgramadaById(id: String): Consulta? {
        return consultaDao.findConsultaProgramadaById(id)?.toDomain()
    }
    suspend fun updateEstadoById(id: String, estado: Estado) {
        return consultaDao.updateEstadoById(id, estado)
    }

    suspend fun sincronizarConsultasBatch(): SyncResult<BatchSyncResult> {
        return try {
            val consultasPendientes = consultaDao.findAllNotSynced()
            android.util.Log.d("ConsultaRepository", "Consultas no sincronizadas encontradas: ${consultasPendientes.size}")
            if (consultasPendientes.isEmpty()) {
                android.util.Log.d("ConsultaRepository", "No hay consultas para sincronizar")
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay consultas para sincronizar"
                )
            }

            android.util.Log.d("ConsultaRepository", "Enviando ${consultasPendientes.size} consultas al servidor")
            val consultasDto = consultasPendientes.map { it.toDto() }
            val response = batchApi.syncConsultasBatch(consultasDto)

            response.toBatchSyncResult { batchResult ->
                batchResult.successfulUuids.forEach { uuid ->
                    consultaDao.markAsSynced(uuid, LocalDateTime.now())
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
                            response.body()?.message ?: "Error en la sincronización de consultas",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de consultas completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

}