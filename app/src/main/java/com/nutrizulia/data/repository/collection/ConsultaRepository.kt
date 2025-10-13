package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.ConsultaDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.pojo.DailyAppointmentCount
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
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
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {

    suspend fun findAllNotSynced(usuarioInstitucionId: Int): Int {
        return consultaDao.countNotSynced(usuarioInstitucionId)
    }

    suspend fun upsert(consulta: Consulta): Long {
        return consultaDao.upsert(consulta.toEntity())
    }
    suspend fun getAppointmentCountsByDay(usuarioInstitucionId: Int): Map<LocalDate, Int> {
        val dailyCounts: List<DailyAppointmentCount> = consultaDao.getAppointmentCountsByDay(usuarioInstitucionId)
        return dailyCounts.associate { dailyCount ->
            dailyCount.date to dailyCount.count
        }
    }
    suspend fun getConsultasDelMesActual(usuarioInstitucionId: Int): List<Consulta> {
        return consultaDao.findConsultasDelMesActual(usuarioInstitucionId).map { it.toDomain() }
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
    
    suspend fun getUltimaConsultaRealizada(usuarioInstitucionId: Int): Consulta? {
        return consultaDao.findUltimaConsultaRealizada(usuarioInstitucionId)?.toDomain()
    }
    suspend fun updateEstadoById(id: String, estado: Estado) {
        return consultaDao.updateEstadoById(id, estado)
    }

    suspend fun findPreviousDayPendingAppointments(): List<Consulta> {
        return consultaDao.findPreviousDayPendingAppointments().map { it.toDomain() }
    }

    suspend fun updatePreviousDayPendingAppointmentsToNoShow(): Int {
        val timestamp: LocalDateTime = LocalDateTime.now()
        return consultaDao.updatePreviousDayPendingAppointmentsToNoShow(timestamp)
    }

    suspend fun sincronizarConsultasBatch(usuarioInstitucionId: Int): SyncResult<BatchSyncResult> {
        return try {
            val consultasPendientes = consultaDao.findAllNotSynced(usuarioInstitucionId)
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

    /**
     * Sincronización completa de consultas desde el backend
     * Recupera todas las consultas del usuario y las guarda localmente
     * @param usuarioInstitucionId ID de la institución del usuario
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncConsultas(): SyncResult<Int> {
        return try {
            android.util.Log.d("ConsultaRepository", "Iniciando sincronización completa de consultas")
            
            val response = fullSyncApi.getFullSyncConsultas()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("ConsultaRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} consultas")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    consultaDao.upsertAll(entidades)
                    
                    android.util.Log.d("ConsultaRepository", "Sincronización completa de consultas exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de consultas exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("ConsultaRepository", "No hay consultas para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay consultas para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ConsultaRepository", "Error en sincronización completa de consultas", e)
            e.toSyncResult()
        }
    }

    // ===== Nuevos métodos para recordatorios locales =====
    suspend fun countPendingOrRescheduled(usuarioInstitucionId: Int): Int {
        return consultaDao.countPendingOrRescheduled(usuarioInstitucionId)
    }

    suspend fun findUpcomingPendingOrRescheduled(usuarioInstitucionId: Int, start: LocalDateTime): List<Consulta> {
        return consultaDao.findUpcomingPendingOrRescheduled(usuarioInstitucionId, start).map { it.toDomain() }
    }

    suspend fun countPendingOrRescheduledBetween(usuarioInstitucionId: Int, start: LocalDateTime, end: LocalDateTime): Int {
        return consultaDao.countPendingOrRescheduledBetween(usuarioInstitucionId, start, end)
    }
}