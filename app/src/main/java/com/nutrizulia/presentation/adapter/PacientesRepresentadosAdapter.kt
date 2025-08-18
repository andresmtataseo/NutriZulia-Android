package com.nutrizulia.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.databinding.ItemPacienteRepresentadoBinding
import com.nutrizulia.domain.model.collection.PacienteRepresentado
import java.time.format.DateTimeFormatter

class PacientesRepresentadosAdapter(
    private var pacientesRepresentados: List<PacienteRepresentado> = emptyList(),
    private val onItemClick: (PacienteRepresentado) -> Unit
) : RecyclerView.Adapter<PacientesRepresentadosAdapter.PacienteRepresentadoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteRepresentadoViewHolder {
        val binding = ItemPacienteRepresentadoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PacienteRepresentadoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PacienteRepresentadoViewHolder, position: Int) {
        holder.bind(pacientesRepresentados[position])
    }

    override fun getItemCount(): Int = pacientesRepresentados.size

    fun updatePacientesRepresentados(newPacientesRepresentados: List<PacienteRepresentado>) {
        pacientesRepresentados = newPacientesRepresentados
        notifyDataSetChanged()
    }

    inner class PacienteRepresentadoViewHolder(private val binding: ItemPacienteRepresentadoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pacienteRepresentado: PacienteRepresentado) {
            binding.apply {
                tvNombreCompleto.text = pacienteRepresentado.nombreCompletoPaciente
                tvCedula.text = "Cédula: ${pacienteRepresentado.pacienteCedula}"
                tvParentesco.text = pacienteRepresentado.parentescoNombre
                tvGenero.text = "Género: ${pacienteRepresentado.pacienteGenero}"
                
                // Formatear fecha de nacimiento
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                tvFechaNacimiento.text = "Fecha de nacimiento: ${pacienteRepresentado.pacienteFechaNacimiento.format(formatter)}"
                
                // Mostrar teléfono si está disponible
                tvTelefono.text = "Teléfono: ${pacienteRepresentado.pacienteTelefono ?: "No disponible"}"
                
                // Calcular y mostrar edad
                val edad = java.time.Period.between(pacienteRepresentado.pacienteFechaNacimiento, java.time.LocalDate.now()).years
                tvEdad.text = "Edad: $edad años"
                
                root.setOnClickListener {
                    onItemClick(pacienteRepresentado)
                }
            }
        }
    }
}