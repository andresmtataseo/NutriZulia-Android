package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.databinding.ItemRepresentanteBinding
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.util.Utils.calcularEdad

class RepresentanteAdapter(
    private var representantes: List<Representante>,
    private val onClickListener: (Representante) -> Unit
) : RecyclerView.Adapter<RepresentanteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentanteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_representante, parent, false)
        return RepresentanteViewHolder(view)
    }

    override fun getItemCount(): Int = representantes.size

    override fun onBindViewHolder(holder: RepresentanteViewHolder, position: Int) {
        val representante = representantes[position]
        holder.bind(representante, onClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRepresentantes(newRepresentantes: List<Representante>) {
        this.representantes = newRepresentantes
        notifyDataSetChanged()
    }

}

class RepresentanteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = ItemRepresentanteBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(representante: Representante, onClickListener: (Representante) -> Unit) {
        binding.tvNombreCompleto.text = "${representante.nombres} ${representante.apellidos}"
        binding.tvCedula.text = "Cédula: ${representante.cedula}"
        binding.tvGenero.text = "Género: ${representante.genero}"
        binding.tvFechaNacimiento.text = "Fecha de nacimiento: ${representante.fechaNacimiento}"
        binding.tvEdad.text = "Edad: ${calcularEdad(representante.fechaNacimiento)} años"
        binding.cardRepresentante.setOnClickListener { onClickListener(representante) }
    }

}