package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.ConsultaDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.pojo.DailyAppointmentCount
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.ConsultaDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.toSyncResult

import java.time.LocalDate
import javax.inject.Inject

class ConsultaRepository @Inject constructor(
    private val consultaDao: ConsultaDao,
    private val api: ICollectionSyncService
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

    suspend fun sincronizarConsultas(): SyncResult<List<ConsultaDto>> {
        return try {
            val consultasPendientes = consultaDao.findAllNotSynced()
            if (consultasPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay consultas para sincronizar")
            }
            val consultasDto = consultasPendientes.map { it.toDto() }
            val response = api.syncConsultas(consultasDto)

            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true
                    )
                    consultaDao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de consultas completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

}