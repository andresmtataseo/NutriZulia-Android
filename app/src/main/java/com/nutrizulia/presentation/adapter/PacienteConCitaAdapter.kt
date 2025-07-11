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
import com.nutrizulia.databinding.ItemCitaBinding
import com.nutrizulia.util.Utils.calcularEdad
import java.time.format.DateTimeFormatter
import java.util.Locale

class PacienteConCitaAdapter(
    private var pacientesConCita: List<PacienteConCita>,
    private val onClickCardCitaListener: (PacienteConCita) -> Unit,
    private val onClickCardConsultaListener: (PacienteConCita) -> Unit,
    private val onClickCitaPerdidaListener: (PacienteConCita) -> Unit
) : RecyclerView.Adapter<PacienteConCitaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteConCitaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return PacienteConCitaViewHolder(view)
    }

    override fun getItemCount(): Int = pacientesConCita.size

    override fun onBindViewHolder(holder: PacienteConCitaViewHolder, position: Int) {
        val citaConPaciente = pacientesConCita[position]
        holder.bind(
            citaConPaciente,
            onClickCardCitaListener,
            onClickCardConsultaListener,
            onClickCitaPerdidaListener
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCitas(newCitasConPacientes: List<PacienteConCita>) {
        this.pacientesConCita = newCitasConPacientes
        notifyDataSetChanged()
    }
}

class PacienteConCitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemCitaBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(
        pacienteConCita: PacienteConCita,
        onCardCitaClick: (PacienteConCita) -> Unit,
        onCardConsultaClick: (PacienteConCita) -> Unit,
        onCardCitaPerdidaClick: (PacienteConCita) -> Unit
    ) = with(binding) {


        tvNombreCompletoPaciente.text = pacienteConCita.nombreCompleto
        tvCedulaPaciente.text = "Cédula: ${pacienteConCita.cedulaPaciente}"
        tvEdad.text = "Edad: ${calcularEdad(pacienteConCita.fechaNacimientoPaciente)} años"
        tvFechaProgramada.text = "Fecha: ${pacienteConCita.fechaHoraProgramadaConsulta?.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
        tvHoraProgramda.text = "Hora: ${pacienteConCita.fechaHoraProgramadaConsulta?.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))}"
        tvEstado.text = pacienteConCita.estadoConsulta.displayValue

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

        // Control de visibilidad
        when (pacienteConCita.estadoConsulta.displayValue) {
            Estado.PENDIENTE.displayValue, Estado.REPROGRAMADA.displayValue -> {
                cardCita.setOnClickListener { onCardCitaClick(pacienteConCita) }
            }
            Estado.COMPLETADA.displayValue, Estado.SIN_PREVIA_CITA.displayValue -> {
                cardCita.setOnClickListener{ onCardConsultaClick(pacienteConCita) }
            }
            Estado.CANCELADA.displayValue, Estado.NO_ASISTIO.displayValue -> {
                cardCita.setOnClickListener { onCardCitaPerdidaClick(pacienteConCita) }
            }

        }

    }
}