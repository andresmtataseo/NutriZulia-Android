package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nutrizulia.data.local.view.HistorialMedicoCompletoView
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * DAO para consultar el historial médico completo del paciente
 * Utiliza la vista HistorialMedicoCompletoView para obtener datos unificados
 */
@Dao
interface HistorialMedicoDao {
    
    /**
     * Obtiene todo el historial médico de un paciente ordenado por fecha descendente
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialCompletoPaciente(pacienteId: String): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene el historial médico de un paciente con paginación
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getHistorialPacientePaginado(
        pacienteId: String,
        limit: Int,
        offset: Int
    ): List<HistorialMedicoCompletoView>
    
    /**
     * Obtiene el historial médico filtrado por tipo de consulta
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND tipo_consulta = :tipoConsulta
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialPorTipoConsulta(
        pacienteId: String,
        tipoConsulta: TipoConsulta
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene el historial médico filtrado por especialidad
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND especialidad_nombre = :especialidad
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialPorEspecialidad(
        pacienteId: String,
        especialidad: String
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene el historial médico filtrado por estado de consulta
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND estado_consulta = :estado
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialPorEstado(
        pacienteId: String,
        estado: Estado
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene el historial médico en un rango de fechas
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND fecha_consulta BETWEEN :fechaInicio AND :fechaFin
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialPorRangoFechas(
        pacienteId: String,
        fechaInicio: LocalDateTime,
        fechaFin: LocalDateTime
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Busca en el historial médico por texto en motivo, observaciones o diagnósticos
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND (
            motivo_consulta LIKE '%' || :textoBusqueda || '%' OR
            observaciones LIKE '%' || :textoBusqueda || '%' OR
            planes LIKE '%' || :textoBusqueda || '%' OR
            diagnosticos LIKE '%' || :textoBusqueda || '%'
        )
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun buscarEnHistorial(
        pacienteId: String,
        textoBusqueda: String
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene las consultas que tienen detalles antropométricos
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND (peso IS NOT NULL OR altura IS NOT NULL OR talla IS NOT NULL)
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialConDatosAntropometricos(
        pacienteId: String
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene las consultas que tienen signos vitales
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND (
            tension_arterial_sistolica IS NOT NULL OR 
            frecuencia_cardiaca IS NOT NULL OR 
            temperatura IS NOT NULL
        )
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialConSignosVitales(
        pacienteId: String
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene las consultas que tienen datos metabólicos
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND (
            glicemia_basal IS NOT NULL OR 
            hemoglobina_glicosilada IS NOT NULL OR 
            colesterol_total IS NOT NULL
        )
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialConDatosMetabolicos(
        pacienteId: String
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene las consultas pediátricas
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND (usa_biberon IS NOT NULL OR tipo_lactancia IS NOT NULL)
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialPediatrico(
        pacienteId: String
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene las consultas obstétricas
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND (
            esta_embarazada IS NOT NULL OR 
            semanas_gestacion IS NOT NULL OR 
            peso_pre_embarazo IS NOT NULL
        )
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialObstetrico(
        pacienteId: String
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene las consultas con evaluaciones antropométricas
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND evaluaciones_antropometricas IS NOT NULL
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun getHistorialConEvaluaciones(
        pacienteId: String
    ): Flow<List<HistorialMedicoCompletoView>>
    
    /**
     * Obtiene una consulta específica del historial
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE consulta_id = :consultaId
    """)
    suspend fun getConsultaDetallada(consultaId: String): HistorialMedicoCompletoView?
    
    /**
     * Obtiene el conteo total de consultas de un paciente
     */
    @Query("""
        SELECT COUNT(*) FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId
    """)
    suspend fun getConteoConsultasPaciente(pacienteId: String): Int
    
    /**
     * Obtiene la última consulta de un paciente
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
        LIMIT 1
    """)
    suspend fun getUltimaConsultaPaciente(pacienteId: String): HistorialMedicoCompletoView?
    
    /**
     * Obtiene las especialidades únicas del historial de un paciente
     */
    @Query("""
        SELECT DISTINCT especialidad_nombre 
        FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND especialidad_nombre IS NOT NULL
        ORDER BY especialidad_nombre ASC
    """)
    suspend fun getEspecialidadesDelPaciente(pacienteId: String): List<String>
    
    /**
     * Obtiene los años únicos del historial de un paciente para filtros
     */
    @Query("""
        SELECT DISTINCT CAST(strftime('%Y', fecha_consulta) AS INTEGER) as anio
        FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND fecha_consulta IS NOT NULL
        ORDER BY anio DESC
    """)
    suspend fun getAniosDelHistorialPaciente(pacienteId: String): List<Int>
    
    /**
     * Búsqueda avanzada con múltiples filtros
     */
    @Query("""
        SELECT * FROM historial_medico_completo 
        WHERE paciente_id = :pacienteId 
        AND (:tipoConsulta IS NULL OR tipo_consulta = :tipoConsulta)
        AND (:especialidad IS NULL OR especialidad_nombre = :especialidad)
        AND (:estado IS NULL OR estado_consulta = :estado)
        AND (:fechaInicio IS NULL OR fecha_consulta >= :fechaInicio)
        AND (:fechaFin IS NULL OR fecha_consulta <= :fechaFin)
        AND (:textoBusqueda IS NULL OR (
            motivo_consulta LIKE '%' || :textoBusqueda || '%' OR
            observaciones LIKE '%' || :textoBusqueda || '%' OR
            planes LIKE '%' || :textoBusqueda || '%' OR
            diagnosticos LIKE '%' || :textoBusqueda || '%'
        ))
        ORDER BY fecha_consulta DESC, ultima_actualizacion DESC
    """)
    fun busquedaAvanzada(
        pacienteId: String,
        tipoConsulta: TipoConsulta? = null,
        especialidad: String? = null,
        estado: Estado? = null,
        fechaInicio: LocalDateTime? = null,
        fechaFin: LocalDateTime? = null,
        textoBusqueda: String? = null
    ): Flow<List<HistorialMedicoCompletoView>>
}