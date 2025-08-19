package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.data.local.view.PacienteConConsultaYDetalles
import com.nutrizulia.databinding.ItemHistoriaPacienteBinding
import java.time.format.DateTimeFormatter
import java.util.Locale

class PacienteConConsulaYDetalleAdapter(
    private var pacientesConConsultaYDetalles: List<PacienteConConsultaYDetalles>,
    private val onClickCardConsultaListener: (PacienteConConsultaYDetalles) -> Unit
) : RecyclerView.Adapter<PacienteConConsultaYDetalleViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PacienteConConsultaYDetalleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historia_paciente, parent, false)
        return PacienteConConsultaYDetalleViewHolder(view)
    }

    override fun getItemCount(): Int = pacientesConConsultaYDetalles.size

    override fun onBindViewHolder(holder: PacienteConConsultaYDetalleViewHolder, position: Int) {
        val citaConPaciente = pacientesConConsultaYDetalles[position]
        holder.bind(
            citaConPaciente,
            onClickCardConsultaListener
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCitas(newCitasConPacientes: List<PacienteConConsultaYDetalles>) {
        this.pacientesConConsultaYDetalles = newCitasConPacientes
        notifyDataSetChanged()
    }
}

class PacienteConConsultaYDetalleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemHistoriaPacienteBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(
        pacienteConConsultaYDetalles: PacienteConConsultaYDetalles,
        onCardConsultaClick: (PacienteConConsultaYDetalles) -> Unit,
    ) = with(binding) {


        tvTipoConsulta.text = pacienteConConsultaYDetalles.tipoConsulta.displayValue
        tvTipoActividad.text =
            "Tipo de actividad: ${pacienteConConsultaYDetalles.tipoActividadNombre}"
        tvEspecialidadRemitente.text =
            "Especialidad remitente: ${pacienteConConsultaYDetalles.especialidadRemitenteNombre}"
        tvFechaProgramada.text =
            "Fecha: ${pacienteConConsultaYDetalles.fechaHoraReal.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
        tvHoraProgramda.text = "Hora: ${
            pacienteConConsultaYDetalles.fechaHoraReal.format(
                DateTimeFormatter.ofPattern(
                    "h:mm a",
                    Locale.US
                )
            )
        }"
        tvEstado.text = pacienteConConsultaYDetalles.estadoConsulta.displayValue

        cardCita.setOnClickListener { onCardConsultaClick(pacienteConConsultaYDetalles) }

    }
}