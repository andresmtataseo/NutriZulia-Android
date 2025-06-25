package com.nutrizulia.data.local.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.data.local.enum.TipoLactancia
import com.nutrizulia.data.local.enum.TipoValorCalculado
import java.time.LocalDate
import java.time.LocalDateTime

@DatabaseView(
    viewName = "pacientes_con_consulta_y_detalles",
    value = """
        SELECT
            p.id AS pacienteId,
            p.nombres || ' ' || p.apellidos AS nombreCompletoPaciente,
            p.genero AS generoPaciente,
            p.fecha_nacimiento AS fechaNacimientoPaciente,

            c.id AS consultaId,
            c.usuario_institucion_id AS consultaUsuarioInstitucionId,
            ta.nombre AS tipoActividadNombre, -- Nombre de la actividad
            er.nombre AS especialidadRemitenteNombre, -- Nombre de la especialidad
            c.tipo_consulta AS tipoConsulta,
            c.observaciones AS observacionesConsulta,
            c.planes AS planConsulta,
            c.fecha_hora_real AS fechaHoraReal,

            d.id AS diagnosticoId,
            rb.nombre AS diagnosticoRiesgoBiologicoNombre, -- Nombre del riesgo biológico
            e.nombre AS diagnosticoEnfermedadNombre, -- Nombre de la enfermedad
            d.is_principal AS diagnosticoEsPrincipal,

            dv.id AS detalleVitalId,
            dv.tension_arterial_sistolica AS tensionSistolica,
            dv.tension_arterial_diastolica AS tensionDiastolica,
            dv.frecuencia_cardiaca AS frecuenciaCardiaca,
            dv.frecuencia_respiratoria AS frecuenciaRespiratoria,
            dv.temperatura AS temperatura,
            dv.saturacion_oxigeno AS saturacionOxigeno,
            dv.pulso AS pulso,

            dm.id AS detalleMetabolicoId,
            dm.glicemia_basal AS glicemiaBasal,
            dm.glicemia_postprandial AS glicemiaPostprandial,
            dm.glicemia_aleatoria AS glicemiaAleatoria,
            dm.hemoglobina_glicosilada AS hemoglobinaGlicosilada,
            dm.trigliceridos AS trigliceridos,
            dm.colesterol_total AS colesterolTotal,
            dm.colesterol_hdl AS colesterolHdl,
            dm.colesterol_ldl AS colesterolLdl,

            dp.id AS detallePediatricoId,
            dp.usa_biberon AS usaBiberon,
            dp.tipo_lactancia AS tipoLactancia,

            obst.id AS detalleObstetricoId,
obst.esta_embarazada AS estaEmbarazada,
obst.fecha_ultima_menstruacion AS fechaUltimaMenstruacion,
obst.semanas_gestacion AS semanasGestacion,
obst.peso_pre_embarazo AS pesoPreEmbarazo,


            da.id AS detalleAntropometricoId,
            da.peso AS pesoAntropometrico,
            da.altura AS alturaAntropometrica,
            da.talla AS tallaAntropometrica,
            da.circunferencia_braquial AS circunferenciaBraquial,
            da.circunferencia_cadera AS circunferenciaCadera,
            da.circunferencia_cintura AS circunferenciaCintura,
            da.perimetro_cefalico AS perimetroCefalico,
            da.pliegue_tricipital AS pliegueTricipital,
            da.pliegue_subescapular AS pliegueSubescapular,

            ea.id AS evaluacionAntropometricaId,
            ea.detalle_antropometrico_id AS evaluacionDetalleAntropometricoId,
            ti.nombre AS evaluacionTipoIndicadorNombre,
            ea.valor_calculado AS evaluacionValorCalculado,
            ea.tipo_valor_calculado AS evaluacionTipoValorCalculado,
            ea.diagnostico_antropometrico AS evaluacionDiagnosticoAntropometrico,
            ea.fecha_evaluacion AS evaluacionFechaEvaluacion

        FROM pacientes AS p
        INNER JOIN consultas AS c ON p.id = c.paciente_id
        LEFT JOIN tipos_actividades AS ta ON c.tipo_actividad_id = ta.id -- JOIN para nombre de actividad
        LEFT JOIN especialidades AS er ON c.especialidad_remitente_id = er.id -- JOIN para nombre de especialidad

        LEFT JOIN diagnosticos AS d ON c.id = d.consulta_id
        LEFT JOIN riesgos_biologicos AS rb ON d.riesgo_biologico_id = rb.id -- JOIN para nombre de riesgo biológico
        LEFT JOIN enfermedades AS e ON d.enfermedad_id = e.id -- JOIN para nombre de enfermedad

        LEFT JOIN detalles_vitales AS dv ON c.id = dv.consulta_id
        LEFT JOIN detalles_metabolicos AS dm ON c.id = dm.consulta_id
        LEFT JOIN detalles_pediatricos AS dp ON c.id = dp.consulta_id
        LEFT JOIN detalles_obstetricias AS obst ON c.id = obst.consulta_id
        LEFT JOIN detalles_antropometricos AS da ON c.id = da.consulta_id
        LEFT JOIN evaluaciones_antropometricas AS ea ON da.id = ea.detalle_antropometrico_id
        LEFT JOIN tipos_indicadores AS ti ON ea.tipo_indicador_id = ti.id -- JOIN para nombre de tipo de indicador

        WHERE c.fecha_hora_real IS NOT NULL
    """
)
data class PacienteConConsultaYDetalles(
    // Datos del Paciente
    @ColumnInfo(name = "pacienteId") val pacienteId: String, // Asumo UUID para paciente.id
    @ColumnInfo(name = "nombreCompletoPaciente") val nombreCompletoPaciente: String,
    @ColumnInfo(name = "generoPaciente") val generoPaciente: String, // O tu Enum si lo tienes
    @ColumnInfo(name = "fechaNacimientoPaciente") val fechaNacimientoPaciente: LocalDate,

    // Datos de la Consulta
    @ColumnInfo(name = "consultaId") val consultaId: String, // UUID
    @ColumnInfo(name = "consultaUsuarioInstitucionId") val consultaUsuarioInstitucionId: Int,
    @ColumnInfo(name = "tipoActividadNombre") val tipoActividadNombre: String?, // Nombre de la actividad, puede ser nulo
    @ColumnInfo(name = "especialidadRemitenteNombre") val especialidadRemitenteNombre: String?, // Nombre de la especialidad, puede ser nulo
    @ColumnInfo(name = "tipoConsulta") val tipoConsulta: TipoConsulta,
    @ColumnInfo(name = "observacionesConsulta") val observacionesConsulta: String?, // TEXT puede ser null
    @ColumnInfo(name = "planConsulta") val planConsulta: String?, // TEXT puede ser null (c.planes en SQL)
    @ColumnInfo(name = "fechaHoraReal") val fechaHoraReal: LocalDateTime,

    // Datos del Diagnóstico (puede ser nulo)
    @ColumnInfo(name = "diagnosticoId") val diagnosticoId: String?, // UUID, puede ser nulo por LEFT JOIN
    @ColumnInfo(name = "diagnosticoRiesgoBiologicoNombre") val diagnosticoRiesgoBiologicoNombre: String?, // Nombre del riesgo, puede ser nulo
    @ColumnInfo(name = "diagnosticoEnfermedadNombre") val diagnosticoEnfermedadNombre: String?, // Nombre de la enfermedad, puede ser nulo
    @ColumnInfo(name = "diagnosticoEsPrincipal") val diagnosticoEsPrincipal: Boolean?, // TINYINT -> Boolean, puede ser nulo

    // Datos de Detalles Vitales (puede ser nulo)
    @ColumnInfo(name = "detalleVitalId") val detalleVitalId: String?, // UUID, puede ser nulo por LEFT JOIN
    @ColumnInfo(name = "tensionSistolica") val tensionSistolica: Int?, // Puede ser nulo
    @ColumnInfo(name = "tensionDiastolica") val tensionDiastolica: Int?, // Puede ser nulo
    @ColumnInfo(name = "frecuenciaCardiaca") val frecuenciaCardiaca: Int?, // Puede ser nulo
    @ColumnInfo(name = "frecuenciaRespiratoria") val frecuenciaRespiratoria: Int?, // Puede ser nulo
    @ColumnInfo(name = "temperatura") val temperatura: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "saturacionOxigeno") val saturacionOxigeno: Int?, // Puede ser nulo
    @ColumnInfo(name = "pulso") val pulso: Int?, // Puede ser nulo

    // Datos de Detalles Metabólicos (puede ser nulo)
    @ColumnInfo(name = "detalleMetabolicoId") val detalleMetabolicoId: String?, // UUID, puede ser nulo por LEFT JOIN
    @ColumnInfo(name = "glicemiaBasal") val glicemiaBasal: Int?, // Puede ser nulo
    @ColumnInfo(name = "glicemiaPostprandial") val glicemiaPostprandial: Int?, // Puede ser nulo
    @ColumnInfo(name = "glicemiaAleatoria") val glicemiaAleatoria: Int?, // Puede ser nulo
    @ColumnInfo(name = "hemoglobinaGlicosilada") val hemoglobinaGlicosilada: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "trigliceridos") val trigliceridos: Int?, // Puede ser nulo
    @ColumnInfo(name = "colesterolTotal") val colesterolTotal: Int?, // Puede ser nulo
    @ColumnInfo(name = "colesterolHdl") val colesterolHdl: Int?, // Puede ser nulo
    @ColumnInfo(name = "colesterolLdl") val colesterolLdl: Int?, // Puede ser nulo

    // Datos de Detalles Pediátricos (puede ser nulo)
    @ColumnInfo(name = "detallePediatricoId") val detallePediatricoId: String?, // UUID, puede ser nulo por LEFT JOIN
    @ColumnInfo(name = "usaBiberon") val usaBiberon: Boolean?, // TINYINT -> Boolean, puede ser nulo
    @ColumnInfo(name = "tipoLactancia") val tipoLactancia: TipoLactancia?, // Enum, puede ser nulo

    // Datos de Detalles Obstétricos (puede ser nulo)
    @ColumnInfo(name = "detalleObstetricoId") val detalleObstetricoId: String?, // UUID, puede ser nulo por LEFT JOIN
    @ColumnInfo(name = "estaEmbarazada") val estaEmbarazada: Boolean?, // TINYINT -> Boolean, puede ser nulo
    @ColumnInfo(name = "fechaUltimaMenstruacion") val fechaUltimaMenstruacion: LocalDate?, // DATE -> LocalDate, puede ser nulo
    @ColumnInfo(name = "semanasGestacion") val semanasGestacion: Int?, // Puede ser nulo
    @ColumnInfo(name = "pesoPreEmbarazo") val pesoPreEmbarazo: Double?, // DECIMAL -> Double, puede ser nulo

    // Datos de Detalles Antropométricos (puede ser nulo)
    @ColumnInfo(name = "detalleAntropometricoId") val detalleAntropometricoId: String?, // UUID, puede ser nulo por LEFT JOIN
    @ColumnInfo(name = "pesoAntropometrico") val pesoAntropometrico: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "alturaAntropometrica") val alturaAntropometrica: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "tallaAntropometrica") val tallaAntropometrica: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "circunferenciaBraquial") val circunferenciaBraquial: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "circunferenciaCadera") val circunferenciaCadera: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "circunferenciaCintura") val circunferenciaCintura: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "perimetroCefalico") val perimetroCefalico: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "pliegueTricipital") val pliegueTricipital: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "pliegueSubescapular") val pliegueSubescapular: Double?, // DECIMAL -> Double, puede ser nulo

    // Datos de Evaluaciones Antropométricas (puede ser nulo)
    @ColumnInfo(name = "evaluacionAntropometricaId") val evaluacionAntropometricaId: String?, // UUID, puede ser nulo por LEFT JOIN
    @ColumnInfo(name = "evaluacionDetalleAntropometricoId") val evaluacionDetalleAntropometricoId: String?, // FK, puede ser nulo
    @ColumnInfo(name = "evaluacionTipoIndicadorNombre") val evaluacionTipoIndicadorNombre: String?, // Nombre del tipo de indicador, puede ser nulo
    @ColumnInfo(name = "evaluacionValorCalculado") val evaluacionValorCalculado: Double?, // DECIMAL -> Double, puede ser nulo
    @ColumnInfo(name = "evaluacionTipoValorCalculado") val evaluacionTipoValorCalculado: TipoValorCalculado?, // Enum, puede ser nulo
    @ColumnInfo(name = "evaluacionDiagnosticoAntropometrico") val evaluacionDiagnosticoAntropometrico: String?, // VARCHAR(255), puede ser nulo
    @ColumnInfo(name = "evaluacionFechaEvaluacion") val evaluacionFechaEvaluacion: LocalDateTime? // DATETIME -> LocalDateTime, puede ser nulo
)