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
import com.nutrizulia.databinding.ItemHistoriaPacienteBinding
import com.nutrizulia.util.Utils.calcularEdad
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoriaPacienteAdapter(
    private var pacientesConCita: List<PacienteConCita>,
    private val onClickCardCitaListener: (PacienteConCita) -> Unit,
    private val onClickCardConsultaListener: (PacienteConCita) -> Unit,
    private val onClickCitaPerdidaListener: (PacienteConCita) -> Unit
) : RecyclerView.Adapter<HistoriaPacienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriaPacienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historia_paciente, parent, false)
        return HistoriaPacienteViewHolder(view)
    }

    override fun getItemCount(): Int = pacientesConCita.size

    override fun onBindViewHolder(holder: HistoriaPacienteViewHolder, position: Int) {
        val citaConPaciente = pacientesConCita[position]
        holder.bind(
            citaConPaciente
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
    ) = with(binding) {

        tvTipoConsulta.text = pacienteConCita.tipoConsulta.displayValue
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

    }
}