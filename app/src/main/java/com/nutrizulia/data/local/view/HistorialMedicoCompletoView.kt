package com.nutrizulia.data.local.view

import androidx.room.DatabaseView
import androidx.room.ColumnInfo
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.data.local.enum.TipoLactancia
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Vista Room que unifica todos los datos del historial médico del paciente
 * Incluye información de consultas, diagnósticos, detalles antropométricos,
 * vitales, metabólicos, pediátricos y obstétricos
 */
@DatabaseView(
    viewName = "historial_medico_completo",
    value = """
        SELECT 
            -- Información básica de la consulta
            c.id as consulta_id,
            c.paciente_id,
            c.fecha_hora_real as fecha_consulta,
            c.fecha_hora_programada,
            c.tipo_consulta,
            c.motivo_consulta,
            c.observaciones,
            c.planes,
            c.estado as estado_consulta,
            
            -- Información del paciente
            p.nombres as paciente_nombres,
            p.apellidos as paciente_apellidos,
            p.cedula as paciente_cedula,
            p.fecha_nacimiento as paciente_fecha_nacimiento,
            p.genero as paciente_genero,
            
            -- Información del tipo de actividad y especialidad
            ta.nombre as tipo_actividad_nombre,
            e.nombre as especialidad_nombre,
            
            -- Diagnósticos (concatenados)
            GROUP_CONCAT(
                CASE WHEN d.id IS NOT NULL THEN
                    COALESCE(enf.nombre, rb.nombre) || 
                    CASE WHEN d.is_principal = 1 THEN ' (Principal)' ELSE '' END
                END, '; '
            ) as diagnosticos,
            
            -- Detalles antropométricos
            da.peso,
            da.altura,
            da.talla,
            da.circunferencia_braquial,
            da.circunferencia_cadera,
            da.circunferencia_cintura,
            da.perimetro_cefalico,
            da.pliegue_tricipital,
            da.pliegue_subescapular,
            
            -- Detalles vitales
            dv.tension_arterial_sistolica,
            dv.tension_arterial_diastolica,
            dv.frecuencia_cardiaca,
            dv.frecuencia_respiratoria,
            dv.temperatura,
            dv.saturacion_oxigeno,
            dv.pulso,
            
            -- Detalles metabólicos
            dm.glicemia_basal,
            dm.glicemia_postprandial,
            dm.glicemia_aleatoria,
            dm.hemoglobina_glicosilada,
            dm.trigliceridos,
            dm.colesterol_total,
            dm.colesterol_hdl,
            dm.colesterol_ldl,
            
            -- Detalles pediátricos
            dp.usa_biberon,
            dp.tipo_lactancia,
            
            -- Detalles obstétricos
            dob.esta_embarazada,
            dob.fecha_ultima_menstruacion,
            dob.semanas_gestacion,
            dob.peso_pre_embarazo,
            
            -- Evaluaciones antropométricas (resumen)
            GROUP_CONCAT(
                CASE WHEN ea.id IS NOT NULL THEN
                    ti.nombre || ': ' || ea.valor_calculado || ' (' || ea.diagnostico_antropometrico || ')'
                END, '; '
            ) as evaluaciones_antropometricas,
            
            -- Metadatos
            c.updated_at as ultima_actualizacion
            
        FROM consultas c
        INNER JOIN pacientes p ON c.paciente_id = p.id
        LEFT JOIN tipos_actividades ta ON c.tipo_actividad_id = ta.id
        LEFT JOIN especialidades e ON c.especialidad_remitente_id = e.id
        
        -- Diagnósticos
        LEFT JOIN diagnosticos d ON c.id = d.consulta_id AND d.is_deleted = 0
        LEFT JOIN enfermedades enf ON d.enfermedad_id = enf.id
        LEFT JOIN riesgos_biologicos rb ON d.riesgo_biologico_id = rb.id
        
        -- Detalles médicos
        LEFT JOIN detalles_antropometricos da ON c.id = da.consulta_id AND da.is_deleted = 0
        LEFT JOIN detalles_vitales dv ON c.id = dv.consulta_id AND dv.is_deleted = 0
        LEFT JOIN detalles_metabolicos dm ON c.id = dm.consulta_id AND dm.is_deleted = 0
        LEFT JOIN detalles_pediatricos dp ON c.id = dp.consulta_id AND dp.is_deleted = 0
        LEFT JOIN detalles_obstetricias dob ON c.id = dob.consulta_id AND dob.is_deleted = 0
        
        -- Evaluaciones antropométricas
        LEFT JOIN evaluaciones_antropometricas ea ON c.id = ea.consulta_id AND ea.is_deleted = 0
        LEFT JOIN tipos_indicadores ti ON ea.tipo_indicador_id = ti.id
        
        WHERE c.is_deleted = 0 AND p.is_deleted = 0
        
        GROUP BY c.id, c.paciente_id, c.fecha_hora_real, c.fecha_hora_programada, 
                 c.tipo_consulta, c.motivo_consulta, c.observaciones, c.planes, 
                 c.estado, p.nombres, p.apellidos, p.cedula, p.fecha_nacimiento, 
                 p.genero, ta.nombre, e.nombre, da.peso, da.altura, da.talla, 
                 da.circunferencia_braquial, da.circunferencia_cadera, 
                 da.circunferencia_cintura, da.perimetro_cefalico, 
                 da.pliegue_tricipital, da.pliegue_subescapular, 
                 dv.tension_arterial_sistolica, dv.tension_arterial_diastolica, 
                 dv.frecuencia_cardiaca, dv.frecuencia_respiratoria, 
                 dv.temperatura, dv.saturacion_oxigeno, dv.pulso, 
                 dm.glicemia_basal, dm.glicemia_postprandial, dm.glicemia_aleatoria, 
                 dm.hemoglobina_glicosilada, dm.trigliceridos, dm.colesterol_total, 
                 dm.colesterol_hdl, dm.colesterol_ldl, dp.usa_biberon, 
                 dp.tipo_lactancia, dob.esta_embarazada, dob.fecha_ultima_menstruacion, 
                 dob.semanas_gestacion, dob.peso_pre_embarazo, c.updated_at
    """
)
data class HistorialMedicoCompletoView(
    // Información básica de la consulta
    @ColumnInfo(name = "consulta_id") val consultaId: String,
    @ColumnInfo(name = "paciente_id") val pacienteId: String,
    @ColumnInfo(name = "fecha_consulta") val fechaConsulta: LocalDateTime?,
    @ColumnInfo(name = "fecha_hora_programada") val fechaHoraProgramada: LocalDateTime?,
    @ColumnInfo(name = "tipo_consulta") val tipoConsulta: TipoConsulta,
    @ColumnInfo(name = "motivo_consulta") val motivoConsulta: String?,
    @ColumnInfo(name = "observaciones") val observaciones: String?,
    @ColumnInfo(name = "planes") val planes: String?,
    @ColumnInfo(name = "estado_consulta") val estadoConsulta: Estado,
    
    // Información del paciente
    @ColumnInfo(name = "paciente_nombres") val pacienteNombres: String,
    @ColumnInfo(name = "paciente_apellidos") val pacienteApellidos: String,
    @ColumnInfo(name = "paciente_cedula") val pacienteCedula: String,
    @ColumnInfo(name = "paciente_fecha_nacimiento") val pacienteFechaNacimiento: LocalDate,
    @ColumnInfo(name = "paciente_genero") val pacienteGenero: String,
    
    // Información del tipo de actividad y especialidad
    @ColumnInfo(name = "tipo_actividad_nombre") val tipoActividadNombre: String?,
    @ColumnInfo(name = "especialidad_nombre") val especialidadNombre: String?,
    
    // Diagnósticos
    @ColumnInfo(name = "diagnosticos") val diagnosticos: String?,
    
    // Detalles antropométricos
    @ColumnInfo(name = "peso") val peso: Double?,
    @ColumnInfo(name = "altura") val altura: Double?,
    @ColumnInfo(name = "talla") val talla: Double?,
    @ColumnInfo(name = "circunferencia_braquial") val circunferenciaBraquial: Double?,
    @ColumnInfo(name = "circunferencia_cadera") val circunferenciaCadera: Double?,
    @ColumnInfo(name = "circunferencia_cintura") val circunferenciaCintura: Double?,
    @ColumnInfo(name = "perimetro_cefalico") val perimetroCefalico: Double?,
    @ColumnInfo(name = "pliegue_tricipital") val pliegueTricipital: Double?,
    @ColumnInfo(name = "pliegue_subescapular") val pliegueSubescapular: Double?,
    
    // Detalles vitales
    @ColumnInfo(name = "tension_arterial_sistolica") val tensionArterialSistolica: Int?,
    @ColumnInfo(name = "tension_arterial_diastolica") val tensionArterialDiastolica: Int?,
    @ColumnInfo(name = "frecuencia_cardiaca") val frecuenciaCardiaca: Int?,
    @ColumnInfo(name = "frecuencia_respiratoria") val frecuenciaRespiratoria: Int?,
    @ColumnInfo(name = "temperatura") val temperatura: Double?,
    @ColumnInfo(name = "saturacion_oxigeno") val saturacionOxigeno: Int?,
    @ColumnInfo(name = "pulso") val pulso: Int?,
    
    // Detalles metabólicos
    @ColumnInfo(name = "glicemia_basal") val glicemiaBasal: Int?,
    @ColumnInfo(name = "glicemia_postprandial") val glicemiaPostprandial: Int?,
    @ColumnInfo(name = "glicemia_aleatoria") val glicemiaAleatoria: Int?,
    @ColumnInfo(name = "hemoglobina_glicosilada") val hemoglobinaGlicosilada: Double?,
    @ColumnInfo(name = "trigliceridos") val trigliceridos: Int?,
    @ColumnInfo(name = "colesterol_total") val colesterolTotal: Int?,
    @ColumnInfo(name = "colesterol_hdl") val colesterolHdl: Int?,
    @ColumnInfo(name = "colesterol_ldl") val colesterolLdl: Int?,
    
    // Detalles pediátricos
    @ColumnInfo(name = "usa_biberon") val usaBiberon: Boolean?,
    @ColumnInfo(name = "tipo_lactancia") val tipoLactancia: TipoLactancia?,
    
    // Detalles obstétricos
    @ColumnInfo(name = "esta_embarazada") val estaEmbarazada: Boolean?,
    @ColumnInfo(name = "fecha_ultima_menstruacion") val fechaUltimaMenstruacion: LocalDate?,
    @ColumnInfo(name = "semanas_gestacion") val semanasGestacion: Int?,
    @ColumnInfo(name = "peso_pre_embarazo") val pesoPreEmbarazo: Double?,
    
    // Evaluaciones antropométricas
    @ColumnInfo(name = "evaluaciones_antropometricas") val evaluacionesAntropometricas: String?,
    
    // Metadatos
    @ColumnInfo(name = "ultima_actualizacion") val ultimaActualizacion: LocalDateTime
)