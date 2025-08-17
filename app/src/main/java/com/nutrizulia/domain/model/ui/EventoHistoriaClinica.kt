package com.nutrizulia.domain.model.ui

import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.data.local.enum.TipoLactancia
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Modelo de datos para representar un evento en la historia clínica del paciente
 * Unifica la información de consultas médicas con todos sus detalles asociados
 */
data class EventoHistoriaClinica(
    val consultaId: String,
    val pacienteId: String,
    val fechaEvento: LocalDateTime?,
    val tipoEvento: TipoEvento,
    val titulo: String,
    val descripcion: String?,
    val estado: Estado,
    val profesional: String?,
    val especialidad: String?,
    val detalles: List<DetalleEvento> = emptyList(),
    val ultimaActualizacion: LocalDateTime
) {
    /**
     * Calcula la edad del paciente en la fecha del evento
     */
    fun calcularEdadEnEvento(fechaNacimiento: LocalDate): Int? {
        return fechaEvento?.let { fecha ->
            val fechaEvento = fecha.toLocalDate()
            var edad = fechaEvento.year - fechaNacimiento.year
            if (fechaEvento.monthValue < fechaNacimiento.monthValue || 
                (fechaEvento.monthValue == fechaNacimiento.monthValue && 
                 fechaEvento.dayOfMonth < fechaNacimiento.dayOfMonth)) {
                edad--
            }
            edad
        }
    }
    
    /**
     * Obtiene el icono asociado al tipo de evento
     */
    fun getIconoEvento(): Int {
        return tipoEvento.icono
    }
    
    /**
     * Obtiene el color asociado al tipo de evento
     */
    fun getColorEvento(): Int {
        return tipoEvento.color
    }
    
    /**
     * Verifica si el evento tiene detalles antropométricos
     */
    fun tieneDetallesAntropometricos(): Boolean {
        return detalles.any { it.categoria == CategoriaDetalle.ANTROPOMETRICO }
    }
    
    /**
     * Verifica si el evento tiene detalles vitales
     */
    fun tieneDetallesVitales(): Boolean {
        return detalles.any { it.categoria == CategoriaDetalle.VITAL }
    }
    
    /**
     * Verifica si el evento tiene detalles metabólicos
     */
    fun tieneDetallesMetabolicos(): Boolean {
        return detalles.any { it.categoria == CategoriaDetalle.METABOLICO }
    }
    
    /**
     * Verifica si el evento tiene diagnósticos
     */
    fun tieneDiagnosticos(): Boolean {
        return detalles.any { it.categoria == CategoriaDetalle.DIAGNOSTICO }
    }
    
    /**
     * Verifica si el evento tiene detalles pediátricos
     */
    fun tieneDetallesPediatricos(): Boolean {
        return detalles.any { it.categoria == CategoriaDetalle.PEDIATRICO }
    }
    
    /**
     * Verifica si el evento tiene detalles obstétricos
     */
    fun tieneDetallesObstetricos(): Boolean {
        return detalles.any { it.categoria == CategoriaDetalle.OBSTETRICO }
    }
    
    /**
     * Verifica si el evento tiene evaluaciones
     */
    fun tieneEvaluaciones(): Boolean {
        return detalles.any { it.categoria == CategoriaDetalle.EVALUACION }
    }
}

/**
 * Enumeración que define los tipos de eventos en la historia clínica
 */
enum class TipoEvento(
    val nombre: String,
    val icono: Int,
    val color: Int
) {
    CONSULTA_GENERAL(
        "Consulta General",
        com.nutrizulia.R.drawable.ic_medical_services,
        com.nutrizulia.R.color.md_theme_primary
    ),
    CONSULTA_ESPECIALIZADA(
        "Consulta Especializada",
        com.nutrizulia.R.drawable.ic_medical_services,
        com.nutrizulia.R.color.md_theme_secondary
    ),
    CONTROL_NUTRICIONAL(
        "Control Nutricional",
        com.nutrizulia.R.drawable.ic_medical_services,
        com.nutrizulia.R.color.success_color
    ),
    EVALUACION_ANTROPOMETRICA(
        "Evaluación Antropométrica",
        com.nutrizulia.R.drawable.ic_person,
        com.nutrizulia.R.color.info_color
    ),
    CONTROL_PEDIATRICO(
        "Control Pediátrico",
        com.nutrizulia.R.drawable.ic_paciente,
        com.nutrizulia.R.color.warning_color
    ),
    CONTROL_OBSTETRICO(
        "Control Obstétrico",
        com.nutrizulia.R.drawable.ic_female,
        com.nutrizulia.R.color.md_theme_tertiary
    );
    
    companion object {
        /**
         * Determina el tipo de evento basado en el tipo de consulta y los detalles disponibles
         */
        fun determinarTipoEvento(
            tipoConsulta: TipoConsulta,
            tieneDetallesPediatricos: Boolean,
            tieneDetallesObstetricos: Boolean,
            tieneEvaluacionAntropometrica: Boolean,
            especialidad: String?
        ): TipoEvento {
            return when {
                tieneDetallesPediatricos -> CONTROL_PEDIATRICO
                tieneDetallesObstetricos -> CONTROL_OBSTETRICO
                tieneEvaluacionAntropometrica -> EVALUACION_ANTROPOMETRICA
                especialidad?.contains("Nutrición", ignoreCase = true) == true -> CONTROL_NUTRICIONAL
                especialidad != null && !especialidad.contains("Nutrición", ignoreCase = true) -> CONSULTA_ESPECIALIZADA
                else -> CONSULTA_GENERAL
            }
        }
    }
}

/**
 * Modelo para representar un detalle específico de un evento
 */
data class DetalleEvento(
    val categoria: CategoriaDetalle,
    val nombre: String,
    val valor: String,
    val unidad: String? = null,
    val esRelevante: Boolean = true
) {
    /**
     * Obtiene la representación completa del detalle
     */
    fun getValorCompleto(): String {
        return if (unidad != null) "$valor $unidad" else valor
    }
}

/**
 * Categorías de detalles médicos
 */
enum class CategoriaDetalle(
    val nombre: String,
    val icono: Int
) {
    DIAGNOSTICO(
        "Diagnósticos",
        com.nutrizulia.R.drawable.ic_medical_services
    ),
    ANTROPOMETRICO(
        "Antropométrico",
        com.nutrizulia.R.drawable.ic_person
    ),
    VITAL(
        "Signos Vitales",
        com.nutrizulia.R.drawable.ic_medical_services
    ),
    METABOLICO(
        "Metabólico",
        com.nutrizulia.R.drawable.ic_medical_services
    ),
    PEDIATRICO(
        "Pediátrico",
        com.nutrizulia.R.drawable.ic_paciente
    ),
    OBSTETRICO(
        "Obstétrico",
        com.nutrizulia.R.drawable.ic_female
    ),
    EVALUACION(
        "Evaluaciones",
        com.nutrizulia.R.drawable.ic_check
    )
}