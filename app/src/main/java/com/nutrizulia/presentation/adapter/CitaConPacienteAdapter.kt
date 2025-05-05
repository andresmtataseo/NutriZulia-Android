package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.databinding.ItemCitaBinding
import com.nutrizulia.domain.model.CitaConPaciente
import com.nutrizulia.util.EstadoCita
import com.nutrizulia.util.Utils.calcularEdad

class CitaConPacienteAdapter(
    private var citasConPacientes: List<CitaConPaciente>,
    private val onClickCardCitaListener: (CitaConPaciente) -> Unit,
    private val onClickCardConsultaListener: (CitaConPaciente) -> Unit,
    private val onClickCitaPerdidaListener: (CitaConPaciente) -> Unit
) : RecyclerView.Adapter<CitaConPacienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaConPacienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return CitaConPacienteViewHolder(view)
    }

    override fun getItemCount(): Int = citasConPacientes.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CitaConPacienteViewHolder, position: Int) {
        val citaConPaciente = citasConPacientes[position]
        holder.bind(
            citaConPaciente,
            onClickCardCitaListener,
            onClickCardConsultaListener,
            onClickCitaPerdidaListener
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCitas(newCitasConPacientes: List<CitaConPaciente>) {
        this.citasConPacientes = newCitasConPacientes
        notifyDataSetChanged()
    }
}

class CitaConPacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemCitaBinding.bind(itemView)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun bind(
        citaConPaciente: CitaConPaciente,
        onCardCitaClick: (CitaConPaciente) -> Unit,
        onCardConsultaClick: (CitaConPaciente) -> Unit,
        onCardCitaPerdidaClick: (CitaConPaciente) -> Unit
    ) = with(binding) {

        val paciente = citaConPaciente.paciente
        val cita = citaConPaciente.cita

        tvNombreCompletoPaciente.text =
            "${paciente.primerNombre} ${paciente.segundoNombre} ${paciente.primerApellido} ${paciente.segundoApellido}"
        tvCedulaPaciente.text = "Cédula: ${paciente.cedula}"
        tvEdad.text = "Edad: ${calcularEdad(paciente.fechaNacimiento)} años"
        tvFechaProgramada.text = "Fecha: ${cita.fechaProgramada}"
        tvHoraProgramda.text = "Hora: ${cita.horaProgramada}"
        tvEstado.text = cita.estado

        val colorResId = when (cita.estado) {
            EstadoCita.PENDIENTE.descripcion,
            EstadoCita.REPROGRAMADA.descripcion -> R.color.color_cita_pendiente
            EstadoCita.COMPLETADA.descripcion -> R.color.color_cita_completada
            EstadoCita.CANCELADA.descripcion,
            EstadoCita.NO_ASISTIO.descripcion -> R.color.color_cita_cancelada
            else -> R.color.color_cita_pendiente
        }

        tvEstado.setTextColor(ContextCompat.getColor(itemView.context, colorResId))

        // Control de visibilidad
        when (cita.estado) {
            EstadoCita.PENDIENTE.descripcion, EstadoCita.REPROGRAMADA.descripcion -> {
                cardCita.setOnClickListener { onCardCitaClick(citaConPaciente) }
            }
            EstadoCita.COMPLETADA.descripcion -> {
                cardCita.setOnClickListener{ onCardConsultaClick(citaConPaciente) }
            }
            EstadoCita.CANCELADA.descripcion, EstadoCita.NO_ASISTIO.descripcion -> {
                cardCita.setOnClickListener { onCardCitaPerdidaClick(citaConPaciente) }
            }

        }

    }
}