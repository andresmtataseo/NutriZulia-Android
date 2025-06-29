package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.databinding.ItemPacienteBinding
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.util.Utils.calcularEdad

class PacienteAdapter(
    private var pacientes: List<Paciente>,
    private val onClickListener: (Paciente) -> Unit
) : RecyclerView.Adapter<PacienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_paciente, parent, false)
        return PacienteViewHolder(view)
    }

    override fun getItemCount(): Int = pacientes.size

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = pacientes[position]
        holder.bind(paciente, onClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePacientes(newPacientes: List<Paciente>) {
        this.pacientes = newPacientes
        notifyDataSetChanged()
    }

}

class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = ItemPacienteBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(paciente: Paciente, onClickListener: (Paciente) -> Unit) {
        binding.tvNombreCompleto.text = "${paciente.nombres} ${paciente.apellidos}"
        binding.tvCedula.text = "Cédula: ${paciente.cedula}"
        binding.tvGenero.text = "Género: ${paciente.genero}"
        binding.tvFechaNacimiento.text = "Fecha de nacimiento: ${paciente.fechaNacimiento}"
        binding.tvEdad.text = "Edad: ${calcularEdad(paciente.fechaNacimiento)}"
        binding.cardPaciente.setOnClickListener { onClickListener(paciente) }
    }
}