package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.databinding.ItemEventoHistoriaClinicaBinding
import com.nutrizulia.domain.model.ui.EventoHistoriaClinica
import com.nutrizulia.domain.model.ui.TipoEvento
import com.nutrizulia.domain.model.ui.CategoriaDetalle
import com.nutrizulia.data.local.enum.Estado
import java.time.format.DateTimeFormatter
import java.util.Locale

class EventoHistoriaClinicaAdapter(
    private var eventos: List<EventoHistoriaClinica>,
    private val onClickListener: (EventoHistoriaClinica) -> Unit
) : RecyclerView.Adapter<EventoHistoriaClinicaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoHistoriaClinicaViewHolder {
        val binding = ItemEventoHistoriaClinicaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventoHistoriaClinicaViewHolder(binding)
    }

    override fun getItemCount(): Int = eventos.size

    override fun onBindViewHolder(holder: EventoHistoriaClinicaViewHolder, position: Int) {
        val evento = eventos[position]
        holder.bind(evento, onClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateEventos(newEventos: List<EventoHistoriaClinica>) {
        this.eventos = newEventos
        notifyDataSetChanged()
    }

    fun addEventos(newEventos: List<EventoHistoriaClinica>) {
        val startPosition = eventos.size
        this.eventos = eventos + newEventos
        notifyItemRangeInserted(startPosition, newEventos.size)
    }

    fun clearEventos() {
        val itemCount = eventos.size
        this.eventos = emptyList()
        notifyItemRangeRemoved(0, itemCount)
    }
}

class EventoHistoriaClinicaViewHolder(private val binding: ItemEventoHistoriaClinicaBinding) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(
        evento: EventoHistoriaClinica,
        onClickListener: (EventoHistoriaClinica) -> Unit
    ) = with(binding) {

        // Configurar fecha y hora real de ejecución
        evento.fechaEvento?.let { fecha ->
            tvFechaConsulta.text = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        } ?: run {
            tvFechaConsulta.text = "Fecha no disponible"
        }

        // Configurar título con tipo de consulta
        val tipoConsultaTexto = when {
            evento.titulo?.contains("Primera", ignoreCase = true) == true -> "Primera Consulta"
            evento.titulo?.contains("Sucesiva", ignoreCase = true) == true -> "Consulta Sucesiva"
            evento.especialidad != null -> "Consulta de ${evento.especialidad}"
            else -> "Consulta General"
        }
        tvTipoConsulta.text = tipoConsultaTexto
        
        // Configurar estado de la consulta
        val estadoTexto = when (evento.estado) {
            Estado.COMPLETADA -> "Completada"
            Estado.PENDIENTE -> "Pendiente"
            Estado.CANCELADA -> "Cancelada"
            Estado.REPROGRAMADA -> "Reprogramada"
            Estado.NO_ASISTIO -> "No Asistió"
            Estado.SIN_PREVIA_CITA -> "Sin Previa Cita"
            else -> "Estado no definido"
        }
        
        // Tipos de datos contenidos con estado
        val tiposDatos = obtenerTiposDatosContenidos(evento)
        val datosYEstado = "$estadoTexto • $tiposDatos"
        tvTiposDatos.text = datosYEstado

        // Configurar icono según el tipo de evento
         val iconoRes = when (evento.tipoEvento) {
             TipoEvento.CONSULTA_ESPECIALIZADA -> R.drawable.ic_medical_services
             TipoEvento.CONTROL_NUTRICIONAL -> R.drawable.ic_medical_services
             TipoEvento.EVALUACION_ANTROPOMETRICA -> R.drawable.ic_medical_services
             TipoEvento.CONTROL_PEDIATRICO -> R.drawable.ic_medical_services
             TipoEvento.CONTROL_OBSTETRICO -> R.drawable.ic_medical_services
             else -> R.drawable.ic_medical_services
         }
        
        ivIconoConsulta.setImageResource(iconoRes)
        
        // Configurar color del icono según estado
        val colorRes = when (evento.estado) {
            Estado.COMPLETADA -> R.color.estado_completada
            Estado.PENDIENTE -> R.color.estado_pendiente
            Estado.CANCELADA -> R.color.estado_cancelada
            Estado.REPROGRAMADA -> R.color.estado_reprogramada
            Estado.NO_ASISTIO -> R.color.estado_no_asistio
            Estado.SIN_PREVIA_CITA -> R.color.estado_sin_previa_cita
            else -> R.color.consulta_general
        }
        
        ivIconoConsulta.setColorFilter(
            ContextCompat.getColor(itemView.context, colorRes),
            PorterDuff.Mode.SRC_IN
        )
        
        // Configurar especialidad con detalles relevantes
        val detallesRelevantes = evento.detalles
            .filter { it.esRelevante }
            .take(2)
            .joinToString(", ") { "${it.nombre}: ${it.valor}${it.unidad?.let { " $it" } ?: ""}" }
        
        tvEspecialidad.text = if (detallesRelevantes.isNotEmpty()) {
            detallesRelevantes
        } else {
            evento.descripcion?.take(100)?.let { "$it..." } ?: "Sin detalles adicionales"
        }
        tvEspecialidad.visibility = View.VISIBLE

        // Click listener
        root.setOnClickListener { onClickListener(evento) }
    }

    private fun obtenerTiposDatosContenidos(evento: EventoHistoriaClinica): String {
        val categorias = evento.detalles.map { it.categoria }.distinct()
        
        if (categorias.isEmpty()) {
            return "Sin datos registrados"
        }
        
        val tiposTexto = categorias.map { categoria ->
            when (categoria) {
                CategoriaDetalle.ANTROPOMETRICO -> "Antropométrico"
                CategoriaDetalle.VITAL -> "Signos Vitales"
                CategoriaDetalle.METABOLICO -> "Metabólico"
                CategoriaDetalle.PEDIATRICO -> "Pediátrico"
                CategoriaDetalle.OBSTETRICO -> "Obstétrico"
                CategoriaDetalle.DIAGNOSTICO -> "Diagnóstico"
                CategoriaDetalle.EVALUACION -> "Evaluación"
            }
        }
        
        return "Datos: ${tiposTexto.joinToString(", ")}"
    }
}