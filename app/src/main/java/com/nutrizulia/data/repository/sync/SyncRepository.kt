package com.nutrizulia.data.repository.sync

import android.util.Log
import com.nutrizulia.data.local.dao.collection.*
import com.nutrizulia.data.remote.api.sync.ISyncService
import com.nutrizulia.data.remote.dto.sync.SyncPushRequest
import com.nutrizulia.util.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio central para la sincronización bidireccional de datos.
 * Implementa la estrategia de "Última Fecha de Sincronización Exitosa" usando timestamps.
 * 
 * Flujo de sincronización:
 * 1. Obtener lastSuccessfulSyncTimestamp de SharedPreferences
 * 2. Registrar syncStartTime actual
 * 3. PUSH: Enviar cambios locales al servidor
 * 4. PULL: Recibir cambios remotos del servidor
 * 5. Actualizar base de datos local con datos del servidor
 * 6. Actualizar lastSuccessfulSyncTimestamp con syncStartTime
 */
@Singleton
class SyncRepository @Inject constructor(
    private val syncService: ISyncService,
    private val syncManager: SyncManager,
    // DAOs para entidades de colección
    private val pacienteDao: PacienteDao,
    private val representanteDao: RepresentanteDao,
    private val consultaDao: ConsultaDao,
    private val detalleAntropometricoDao: DetalleAntropometricoDao,
    private val detalleVitalDao: DetalleVitalDao,
    private val detalleMetabolicoDao: DetalleMetabolicoDao,
    private val detallePediatricoDao: DetallePediatricoDao,
    private val detalleObstetriciaDao: DetalleObstetriciaDao,
    private val evaluacionAntropometricaDao: EvaluacionAntropometricaDao,
    private val diagnosticoDao: DiagnosticoDao,
    private val pacienteRepresentanteDao: PacienteRepresentanteDao,
    private val actividadDao: ActividadDao
) {

    companion object {
        private const val TAG = "SyncRepository"
        private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    /**
     * Función principal de sincronización.
     * Ejecuta el proceso completo de sincronización bidireccional.
     * 
     * @return Result<Unit> indicando éxito o fallo de la operación
     */
    suspend fun synchronize(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando proceso de sincronización")
            
            // 1. Obtener el timestamp de la última sincronización exitosa
            val lastSuccessfulSyncTimestamp = syncManager.getLastSuccessfulSyncTimestamp()
            Log.d(TAG, "Última sincronización exitosa: $lastSuccessfulSyncTimestamp")
            
            // 2. Registrar el tiempo de inicio de esta sincronización
            val syncStartTime = LocalDateTime.now()
            Log.d(TAG, "Tiempo de inicio de sincronización: $syncStartTime")
            
            // 3. Fase PUSH: Enviar cambios locales al servidor
            val pushResult = pushLocalChanges(lastSuccessfulSyncTimestamp)
            if (!pushResult.isSuccess) {
                Log.e(TAG, "Error en fase PUSH: ${pushResult.exceptionOrNull()?.message}")
                return@withContext Result.failure(pushResult.exceptionOrNull() ?: Exception("Error en fase PUSH"))
            }
            
            val totalEnviados = pushResult.getOrNull() ?: 0
            Log.d(TAG, "Fase PUSH completada. Registros enviados: $totalEnviados")
            
            // 4. Fase PULL: Recibir cambios del servidor
            val pullResult = pullRemoteChanges(lastSuccessfulSyncTimestamp)
            if (!pullResult.isSuccess) {
                Log.e(TAG, "Error en fase PULL: ${pullResult.exceptionOrNull()?.message}")
                return@withContext Result.failure(pullResult.exceptionOrNull() ?: Exception("Error en fase PULL"))
            }
            
            val (totalInsertados, totalActualizados) = pullResult.getOrNull() ?: (0 to 0)
            Log.d(TAG, "Fase PULL completada. Insertados: $totalInsertados, Actualizados: $totalActualizados")
            
            // 5. Actualizar timestamp de última sincronización exitosa
            syncManager.saveLastSuccessfulSyncTimestamp(syncStartTime)
            Log.d(TAG, "Sincronización completada exitosamente")
            
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error durante la sincronización", e)
            Result.failure(e)
        }
    }

    /**
     * Fase PUSH: Envía los cambios locales al servidor.
     * Obtiene todos los registros modificados desde el último timestamp y los envía.
     */
    private suspend fun pushLocalChanges(lastSyncTimestamp: LocalDateTime): Result<Int> {
        return try {
            Log.d(TAG, "Iniciando fase PUSH")
            
            // Obtener todos los registros modificados localmente
            val pendingPacientes = pacienteDao.findPendingChanges(lastSyncTimestamp)
            val pendingRepresentantes = representanteDao.findPendingChanges(lastSyncTimestamp)
            val pendingConsultas = consultaDao.findPendingChanges(lastSyncTimestamp)
            val pendingDetallesAntropometricos = detalleAntropometricoDao.findPendingChanges(lastSyncTimestamp)
            val pendingDetallesVitales = detalleVitalDao.findPendingChanges(lastSyncTimestamp)
            val pendingDetallesMetabolicos = detalleMetabolicoDao.findPendingChanges(lastSyncTimestamp)
            val pendingDetallesPediatricos = detallePediatricoDao.findPendingChanges(lastSyncTimestamp)
            val pendingDetallesObstetricias = detalleObstetriciaDao.findPendingChanges(lastSyncTimestamp)
            val pendingEvaluacionesAntropometricas = evaluacionAntropometricaDao.findPendingChanges(lastSyncTimestamp)
            val pendingDiagnosticos = diagnosticoDao.findPendingChanges(lastSyncTimestamp)
            val pendingPacientesRepresentantes = pacienteRepresentanteDao.findPendingChanges(lastSyncTimestamp)
            val pendingActividades = actividadDao.findPendingChanges(lastSyncTimestamp)
            
            val totalPendingChanges = pendingPacientes.size + pendingRepresentantes.size + 
                pendingConsultas.size + pendingDetallesAntropometricos.size + 
                pendingDetallesVitales.size + pendingDetallesMetabolicos.size +
                pendingDetallesPediatricos.size + pendingDetallesObstetricias.size +
                pendingEvaluacionesAntropometricas.size + pendingDiagnosticos.size +
                pendingPacientesRepresentantes.size + pendingActividades.size
            
            Log.d(TAG, "Total de cambios pendientes: $totalPendingChanges")
            
            if (totalPendingChanges == 0) {
                Log.d(TAG, "No hay cambios pendientes para enviar")
                return Result.success(0)
            }
            
            // Crear el request con todos los cambios
            val pushRequest = SyncPushRequest(
                pacientes = pendingPacientes,
                representantes = pendingRepresentantes,
                consultas = pendingConsultas,
                detallesAntropometricos = pendingDetallesAntropometricos,
                detallesVitales = pendingDetallesVitales,
                detallesMetabolicos = pendingDetallesMetabolicos,
                detallesPediatricos = pendingDetallesPediatricos,
                detallesObstetricias = pendingDetallesObstetricias,
                evaluacionesAntropometricas = pendingEvaluacionesAntropometricas,
                diagnosticos = pendingDiagnosticos,
                pacientesRepresentantes = pendingPacientesRepresentantes,
                actividades = pendingActividades
            )
            
            // Enviar al servidor
            val response = syncService.pushChanges(pushRequest)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Cambios enviados exitosamente al servidor")
                Result.success(totalPendingChanges)
            } else {
                val errorMsg = "Error al enviar cambios: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en fase PUSH", e)
            Result.failure(e)
        }
    }

    /**
     * Fase PULL: Recibe los cambios del servidor y actualiza la base de datos local.
     * Utiliza operaciones upsert para insertar o actualizar según corresponda.
     */
    private suspend fun pullRemoteChanges(lastSyncTimestamp: LocalDateTime): Result<Pair<Int, Int>> {
        return try {
            Log.d(TAG, "Iniciando fase PULL")
            
            val timestampString = lastSyncTimestamp.format(DATE_FORMATTER)
            val response = syncService.pullChanges(timestampString)
            
            if (!response.isSuccessful) {
                val errorMsg = "Error al obtener cambios del servidor: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                return Result.failure(Exception(errorMsg))
            }
            
            val pullResponse = response.body()
            if (pullResponse == null) {
                Log.e(TAG, "Respuesta del servidor es nula")
                return Result.failure(Exception("Respuesta del servidor es nula"))
            }
            
            var totalInsertados = 0
            var totalActualizados = 0
            
            // Actualizar cada tipo de entidad usando upsert
            // Esto sobreescribirá los updated_at locales con los valores autoritativos del servidor
            
            if (pullResponse.pacientes.isNotEmpty()) {
                pacienteDao.upsertAll(pullResponse.pacientes)
                totalInsertados += pullResponse.pacientes.size
                Log.d(TAG, "Actualizados ${pullResponse.pacientes.size} pacientes")
            }
            
            if (pullResponse.representantes.isNotEmpty()) {
                representanteDao.upsertAll(pullResponse.representantes)
                totalInsertados += pullResponse.representantes.size
                Log.d(TAG, "Actualizados ${pullResponse.representantes.size} representantes")
            }
            
            if (pullResponse.consultas.isNotEmpty()) {
                consultaDao.upsertAll(pullResponse.consultas)
                totalInsertados += pullResponse.consultas.size
                Log.d(TAG, "Actualizadas ${pullResponse.consultas.size} consultas")
            }
            
            if (pullResponse.detallesAntropometricos.isNotEmpty()) {
                detalleAntropometricoDao.upsertAll(pullResponse.detallesAntropometricos)
                totalInsertados += pullResponse.detallesAntropometricos.size
                Log.d(TAG, "Actualizados ${pullResponse.detallesAntropometricos.size} detalles antropométricos")
            }
            
            if (pullResponse.detallesVitales.isNotEmpty()) {
                detalleVitalDao.upsertAll(pullResponse.detallesVitales)
                totalInsertados += pullResponse.detallesVitales.size
                Log.d(TAG, "Actualizados ${pullResponse.detallesVitales.size} detalles vitales")
            }
            
            if (pullResponse.detallesMetabolicos.isNotEmpty()) {
                detalleMetabolicoDao.upsertAll(pullResponse.detallesMetabolicos)
                totalInsertados += pullResponse.detallesMetabolicos.size
                Log.d(TAG, "Actualizados ${pullResponse.detallesMetabolicos.size} detalles metabólicos")
            }
            
            if (pullResponse.detallesPediatricos.isNotEmpty()) {
                detallePediatricoDao.upsertAll(pullResponse.detallesPediatricos)
                totalInsertados += pullResponse.detallesPediatricos.size
                Log.d(TAG, "Actualizados ${pullResponse.detallesPediatricos.size} detalles pediátricos")
            }
            
            if (pullResponse.detallesObstetricias.isNotEmpty()) {
                detalleObstetriciaDao.upsertAll(pullResponse.detallesObstetricias)
                totalInsertados += pullResponse.detallesObstetricias.size
                Log.d(TAG, "Actualizados ${pullResponse.detallesObstetricias.size} detalles de obstetricia")
            }
            
            if (pullResponse.evaluacionesAntropometricas.isNotEmpty()) {
                evaluacionAntropometricaDao.upsertAll(pullResponse.evaluacionesAntropometricas)
                totalInsertados += pullResponse.evaluacionesAntropometricas.size
                Log.d(TAG, "Actualizadas ${pullResponse.evaluacionesAntropometricas.size} evaluaciones antropométricas")
            }
            
            if (pullResponse.diagnosticos.isNotEmpty()) {
                diagnosticoDao.upsertAll(pullResponse.diagnosticos)
                totalInsertados += pullResponse.diagnosticos.size
                Log.d(TAG, "Actualizados ${pullResponse.diagnosticos.size} diagnósticos")
            }
            
            if (pullResponse.pacientesRepresentantes.isNotEmpty()) {
                pacienteRepresentanteDao.upsertAll(pullResponse.pacientesRepresentantes)
                totalInsertados += pullResponse.pacientesRepresentantes.size
                Log.d(TAG, "Actualizadas ${pullResponse.pacientesRepresentantes.size} relaciones paciente-representante")
            }
            
            if (pullResponse.actividades.isNotEmpty()) {
                actividadDao.upsertAll(pullResponse.actividades)
                totalInsertados += pullResponse.actividades.size
                Log.d(TAG, "Actualizadas ${pullResponse.actividades.size} actividades")
            }
            
            Log.d(TAG, "Fase PULL completada. Total registros procesados: $totalInsertados")
            Result.success(totalInsertados to totalActualizados)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en fase PULL", e)
            Result.failure(e)
        }
    }
}