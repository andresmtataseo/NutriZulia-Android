package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.databinding.ItemHistoriaPacienteBinding
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoriaPacienteAdapter(
    private var pacientesConCita: List<PacienteConCita>,
    private val onClickCardConsultaListener: (PacienteConCita) -> Unit
) : RecyclerView.Adapter<HistoriaPacienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriaPacienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historia_paciente, parent, false)
        return HistoriaPacienteViewHolder(view)
    }

    override fun getItemCount(): Int = pacientesConCita.size

    override fun onBindViewHolder(holder: HistoriaPacienteViewHolder, position: Int) {
        val citaConPaciente = pacientesConCita[position]
        holder.bind(
            citaConPaciente,
            onClickCardConsultaListener,
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCitas(newCitasConPacientes: List<PacienteConCita>) {
        this.pacientesConCita = newCitasConPacientes
        notifyDataSetChanged()
    }
}

class HistoriaPacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemHistoriaPacienteBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(
        pacienteConCita: PacienteConCita,
        onCardConsultaClick: (PacienteConCita) -> Unit
    ) = with(binding) {

        tvTipoConsulta.text = pacienteConCita.tipoConsulta.displayValue
        tvTipoActividad.text = "Tipo de actividad: " + pacienteConCita.nombreTipoActividad
        tvEspecialidadRemitente.text =
            "Especialidad remitente: " + pacienteConCita.nombreEspecialidadRemitente
        tvEstado.text = pacienteConCita.estadoConsulta.displayValue

        // Mostrar fecha y hora programada en un solo TextView
        if (pacienteConCita.fechaHoraProgramadaConsulta != null) {
            val fechaProgramada =
                pacienteConCita.fechaHoraProgramadaConsulta.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val horaProgramada = pacienteConCita.fechaHoraProgramadaConsulta.format(
                DateTimeFormatter.ofPattern(
                    "h:mm a",
                    Locale.US
                )
            )
            tvFechaProgramada.text = "Fecha programada: $fechaProgramada $horaProgramada"
        } else {
            tvFechaProgramada.text = "Fecha programada: No disponible"
        }

        // Mostrar fecha y hora realizada en un solo TextView
        if (pacienteConCita.fechaHoraRealConsulta != null) {
            val fechaRealizada =
                pacienteConCita.fechaHoraRealConsulta.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val horaRealizada = pacienteConCita.fechaHoraRealConsulta.format(
                DateTimeFormatter.ofPattern(
                    "h:mm a",
                    Locale.US
                )
            )
            tvFechaRealizada.text = "Fecha realizada: $fechaRealizada $horaRealizada"
        } else {
            tvFechaRealizada.text = "Fecha realizada: No disponible"
        }


        val colorResId = when (pacienteConCita.estadoConsulta.displayValue) {
            Estado.PENDIENTE.displayValue,
            Estado.REPROGRAMADA.displayValue -> R.color.color_cita_pendiente

            Estado.COMPLETADA.displayValue,
            Estado.SIN_PREVIA_CITA.displayValue -> R.color.color_cita_completada

            Estado.CANCELADA.displayValue,
            Estado.NO_ASISTIO.displayValue -> R.color.color_cita_cancelada

            else -> R.color.color_cita_pendiente
        }

        tvEstado.setTextColor(ContextCompat.getColor(itemView.context, colorResId))

        cardCita.setOnClickListener { onCardConsultaClick(pacienteConCita) }

    }
}