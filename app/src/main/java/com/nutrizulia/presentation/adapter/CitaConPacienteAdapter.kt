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
    private val onClickCardListener: (CitaConPaciente) -> Unit,
    private val onClickReagendarListener: (CitaConPaciente) -> Unit,
    private val onClickVerMasListener: (CitaConPaciente) -> Unit
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
            onClickCardListener,
            onClickReagendarListener,
            onClickVerMasListener
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
        onCardClick: (CitaConPaciente) -> Unit,
        onReagendarClick: (CitaConPaciente) -> Unit,
        onVerMasClick: (CitaConPaciente) -> Unit
    ) = with(binding) {

        val paciente = citaConPaciente.paciente
        val cita = citaConPaciente.cita

        tvNombreCompletoPaciente.text =
            "${paciente.primerNombre} ${paciente.segundoNombre} ${paciente.primerApellido} ${paciente.segundoApellido}"
        tvCedulaPaciente.text = "Cédula: ${paciente.cedula}"
        tvEdad.text = "Edad: ${calcularEdad(paciente.fechaNacimiento)}"
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
                btnReagendar.visibility = View.VISIBLE
                btnVerMas.visibility = View.VISIBLE
                cardCita.setOnClickListener { onCardClick(citaConPaciente) }
            }
            EstadoCita.CANCELADA.descripcion,
            EstadoCita.NO_ASISTIO.descripcion,
            EstadoCita.COMPLETADA.descripcion -> {
                btnReagendar.visibility = View.GONE
                btnVerMas.visibility = View.VISIBLE
            }
            else -> {
                btnReagendar.visibility = View.VISIBLE
                btnVerMas.visibility = View.VISIBLE
            }
        }

        // Asignar listeners correctos
        btnReagendar.setOnClickListener { onReagendarClick(citaConPaciente) }
        btnVerMas.setOnClickListener { onVerMasClick(citaConPaciente) }
    }
}