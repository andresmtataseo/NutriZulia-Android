package com.nutrizulia.domain.mapper

import com.nutrizulia.data.local.view.HistorialMedicoCompletoView
import com.nutrizulia.domain.model.ui.EventoHistoriaClinica
import com.nutrizulia.domain.model.ui.TipoEvento
import com.nutrizulia.domain.model.ui.DetalleEvento
import com.nutrizulia.domain.model.ui.CategoriaDetalle
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Mapper para convertir datos de la vista Room a modelos de UI
 */
object HistorialMedicoMapper {
    
    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    
    /**
     * Convierte una vista de historial médico completo a un evento de historia clínica
     */
    fun mapToEventoHistoriaClinica(view: HistorialMedicoCompletoView): EventoHistoriaClinica {
        val detalles = mutableListOf<DetalleEvento>()
        
        // Agregar diagnósticos
        view.diagnosticos?.takeIf { it.isNotBlank() }?.let { diagnosticos ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.DIAGNOSTICO,
                    nombre = "Diagnósticos",
                    valor = diagnosticos,
                    esRelevante = true
                )
            )
        }
        
        // Agregar detalles antropométricos
        addDetallesAntropometricos(view, detalles)
        
        // Agregar detalles vitales
        addDetallesVitales(view, detalles)
        
        // Agregar detalles metabólicos
        addDetallesMetabolicos(view, detalles)
        
        // Agregar detalles pediátricos
        addDetallesPediatricos(view, detalles)
        
        // Agregar detalles obstétricos
        addDetallesObstetricos(view, detalles)
        
        // Agregar evaluaciones antropométricas
        view.evaluacionesAntropometricas?.takeIf { it.isNotBlank() }?.let { evaluaciones ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.EVALUACION,
                    nombre = "Evaluaciones Antropométricas",
                    valor = evaluaciones,
                    esRelevante = true
                )
            )
        }
        
        // Determinar tipo de evento
        val tipoEvento = TipoEvento.determinarTipoEvento(
            tipoConsulta = view.tipoConsulta,
            tieneDetallesPediatricos = view.usaBiberon != null || view.tipoLactancia != null,
            tieneDetallesObstetricos = view.estaEmbarazada != null,
            tieneEvaluacionAntropometrica = !view.evaluacionesAntropometricas.isNullOrBlank(),
            especialidad = view.especialidadNombre
        )
        
        // Crear título y descripción
        val titulo = createTitulo(view, tipoEvento)
        val descripcion = createDescripcion(view)
        
        return EventoHistoriaClinica(
            consultaId = view.consultaId,
            pacienteId = view.pacienteId,
            fechaEvento = view.fechaConsulta,
            tipoEvento = tipoEvento,
            titulo = titulo,
            descripcion = descripcion,
            estado = view.estadoConsulta,
            profesional = null, // Se puede agregar información del profesional si está disponible
            especialidad = view.especialidadNombre,
            detalles = detalles,
            ultimaActualizacion = view.ultimaActualizacion
        )
    }
    
    /**
     * Agrega detalles antropométricos a la lista de detalles
     */
    private fun addDetallesAntropometricos(
        view: HistorialMedicoCompletoView,
        detalles: MutableList<DetalleEvento>
    ) {
        view.peso?.let { peso ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.ANTROPOMETRICO,
                    nombre = "Peso",
                    valor = String.format("%.1f", peso),
                    unidad = "kg",
                    esRelevante = true
                )
            )
        }
        
        view.altura?.let { altura ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.ANTROPOMETRICO,
                    nombre = "Altura",
                    valor = String.format("%.2f", altura),
                    unidad = "m",
                    esRelevante = true
                )
            )
        }
        
        view.talla?.let { talla ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.ANTROPOMETRICO,
                    nombre = "Talla",
                    valor = String.format("%.1f", talla),
                    unidad = "cm",
                    esRelevante = true
                )
            )
        }
        
        view.circunferenciaCintura?.let { cintura ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.ANTROPOMETRICO,
                    nombre = "Circunferencia de Cintura",
                    valor = String.format("%.1f", cintura),
                    unidad = "cm",
                    esRelevante = false
                )
            )
        }
        
        view.circunferenciaCadera?.let { cadera ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.ANTROPOMETRICO,
                    nombre = "Circunferencia de Cadera",
                    valor = String.format("%.1f", cadera),
                    unidad = "cm",
                    esRelevante = false
                )
            )
        }
    }
    
    /**
     * Agrega detalles vitales a la lista de detalles
     */
    private fun addDetallesVitales(
        view: HistorialMedicoCompletoView,
        detalles: MutableList<DetalleEvento>
    ) {
        if (view.tensionArterialSistolica != null && view.tensionArterialDiastolica != null) {
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.VITAL,
                    nombre = "Tensión Arterial",
                    valor = "${view.tensionArterialSistolica}/${view.tensionArterialDiastolica}",
                    unidad = "mmHg",
                    esRelevante = true
                )
            )
        }
        
        view.frecuenciaCardiaca?.let { fc ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.VITAL,
                    nombre = "Frecuencia Cardíaca",
                    valor = fc.toString(),
                    unidad = "lpm",
                    esRelevante = true
                )
            )
        }
        
        view.temperatura?.let { temp ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.VITAL,
                    nombre = "Temperatura",
                    valor = String.format("%.1f", temp),
                    unidad = "°C",
                    esRelevante = true
                )
            )
        }
        
        view.saturacionOxigeno?.let { sat ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.VITAL,
                    nombre = "Saturación de Oxígeno",
                    valor = sat.toString(),
                    unidad = "%",
                    esRelevante = true
                )
            )
        }
    }
    
    /**
     * Agrega detalles metabólicos a la lista de detalles
     */
    private fun addDetallesMetabolicos(
        view: HistorialMedicoCompletoView,
        detalles: MutableList<DetalleEvento>
    ) {
        view.glicemiaBasal?.let { glicemia ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.METABOLICO,
                    nombre = "Glicemia Basal",
                    valor = glicemia.toString(),
                    unidad = "mg/dL",
                    esRelevante = true
                )
            )
        }
        
        view.hemoglobinaGlicosilada?.let { hba1c ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.METABOLICO,
                    nombre = "Hemoglobina Glicosilada",
                    valor = String.format("%.1f", hba1c),
                    unidad = "%",
                    esRelevante = true
                )
            )
        }
        
        view.colesterolTotal?.let { colesterol ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.METABOLICO,
                    nombre = "Colesterol Total",
                    valor = colesterol.toString(),
                    unidad = "mg/dL",
                    esRelevante = false
                )
            )
        }
        
        view.trigliceridos?.let { trigliceridos ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.METABOLICO,
                    nombre = "Triglicéridos",
                    valor = trigliceridos.toString(),
                    unidad = "mg/dL",
                    esRelevante = false
                )
            )
        }
    }
    
    /**
     * Agrega detalles pediátricos a la lista de detalles
     */
    private fun addDetallesPediatricos(
        view: HistorialMedicoCompletoView,
        detalles: MutableList<DetalleEvento>
    ) {
        view.tipoLactancia?.let { lactancia ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.PEDIATRICO,
                    nombre = "Tipo de Lactancia",
                    valor = lactancia.name,
                    esRelevante = true
                )
            )
        }
        
        view.usaBiberon?.let { usaBiberon ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.PEDIATRICO,
                    nombre = "Usa Biberón",
                    valor = if (usaBiberon) "Sí" else "No",
                    esRelevante = true
                )
            )
        }
    }
    
    /**
     * Agrega detalles obstétricos a la lista de detalles
     */
    private fun addDetallesObstetricos(
        view: HistorialMedicoCompletoView,
        detalles: MutableList<DetalleEvento>
    ) {
        view.estaEmbarazada?.let { embarazada ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.OBSTETRICO,
                    nombre = "Estado de Embarazo",
                    valor = if (embarazada) "Embarazada" else "No embarazada",
                    esRelevante = true
                )
            )
        }
        
        view.semanasGestacion?.let { semanas ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.OBSTETRICO,
                    nombre = "Semanas de Gestación",
                    valor = semanas.toString(),
                    unidad = "semanas",
                    esRelevante = true
                )
            )
        }
        
        view.pesoPreEmbarazo?.let { peso ->
            detalles.add(
                DetalleEvento(
                    categoria = CategoriaDetalle.OBSTETRICO,
                    nombre = "Peso Pre-embarazo",
                    valor = String.format("%.1f", peso),
                    unidad = "kg",
                    esRelevante = false
                )
            )
        }
    }
    
    /**
     * Crea el título del evento
     */
    private fun createTitulo(view: HistorialMedicoCompletoView, tipoEvento: TipoEvento): String {
        return when (tipoEvento) {
            TipoEvento.CONSULTA_ESPECIALIZADA -> {
                view.especialidadNombre?.let { "Consulta de $it" } ?: "Consulta Especializada"
            }
            TipoEvento.CONTROL_NUTRICIONAL -> "Control Nutricional"
            TipoEvento.EVALUACION_ANTROPOMETRICA -> "Evaluación Antropométrica"
            TipoEvento.CONTROL_PEDIATRICO -> "Control Pediátrico"
            TipoEvento.CONTROL_OBSTETRICO -> "Control Obstétrico"
            else -> view.tipoActividadNombre ?: "Consulta General"
        }
    }
    
    /**
     * Crea la descripción del evento
     */
    private fun createDescripcion(view: HistorialMedicoCompletoView): String? {
        val partes = mutableListOf<String>()
        
        view.motivoConsulta?.takeIf { it.isNotBlank() }?.let { motivo ->
            partes.add("Motivo: $motivo")
        }
        
        view.observaciones?.takeIf { it.isNotBlank() }?.let { observaciones ->
            partes.add("Observaciones: $observaciones")
        }
        
        view.planes?.takeIf { it.isNotBlank() }?.let { planes ->
            partes.add("Planes: $planes")
        }
        
        return if (partes.isNotEmpty()) partes.joinToString(". ") else null
    }
}