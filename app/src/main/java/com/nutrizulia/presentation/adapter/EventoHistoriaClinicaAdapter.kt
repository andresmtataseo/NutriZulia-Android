package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.databinding.ItemEventoHistoriaClinicaBinding
import com.nutrizulia.domain.model.ui.EventoHistoriaClinica
import com.nutrizulia.domain.model.ui.TipoEvento
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

        // Fecha de la consulta
        tvFechaConsulta.text = evento.fechaEvento?.format(
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        )

        // Tipo de consulta (basado en el título del evento)
        tvTipoConsulta.text = evento.titulo ?: "Consulta Médica"
        
        // Tipos de datos contenidos
        val tiposDatos = obtenerTiposDatosContenidos(evento)
        tvTiposDatos.text = tiposDatos

        // Icono de consulta médica
        ivIconoConsulta.setImageResource(R.drawable.ic_medical_services)
        
        // Mostrar especialidad si está disponible
        if (!evento.especialidad.isNullOrBlank()) {
            tvEspecialidad.text = evento.especialidad
            tvEspecialidad.visibility = View.VISIBLE
        } else {
            tvEspecialidad.visibility = View.GONE
        }

        // Click listener
        root.setOnClickListener { onClickListener(evento) }
    }

    private fun obtenerTiposDatosContenidos(evento: EventoHistoriaClinica): String {
        val tiposDatos = mutableListOf<String>()
        
        if (evento.tieneDiagnosticos()) tiposDatos.add("Diagnóstico")
        if (evento.tieneDetallesAntropometricos()) tiposDatos.add("Antropométrico")
        if (evento.tieneDetallesVitales()) tiposDatos.add("Signos Vitales")
        if (evento.tieneDetallesMetabolicos()) tiposDatos.add("Metabólico")
        if (evento.tieneDetallesPediatricos()) tiposDatos.add("Pediátrico")
        if (evento.tieneDetallesObstetricos()) tiposDatos.add("Obstétrico")
        if (evento.tieneEvaluaciones()) tiposDatos.add("Evaluación Antropométrica")
        
        return if (tiposDatos.isNotEmpty()) {
            tiposDatos.joinToString(", ")
        } else {
            "Sin datos específicos"
        }
    }
}