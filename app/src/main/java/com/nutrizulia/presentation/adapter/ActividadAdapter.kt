package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.databinding.ItemActividadBinding

class ActividadAdapter(
    private var actividadesConTipo: List<ActividadConTipo>,
    private val onClickListener: (ActividadConTipo) -> Unit
) : RecyclerView.Adapter<ActividadConTipoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadConTipoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_actividad, parent, false)
        return ActividadConTipoViewHolder(view)
    }

    override fun getItemCount(): Int = actividadesConTipo.size

    override fun onBindViewHolder(holder: ActividadConTipoViewHolder, position: Int) {
        val actividad = actividadesConTipo[position]
        holder.bind(actividad, onClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateActividades(newActividadesConTipo: List<ActividadConTipo>) {
        this.actividadesConTipo = newActividadesConTipo
        notifyDataSetChanged()
    }

}

class ActividadConTipoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = ItemActividadBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(actividad: ActividadConTipo, onClickListener: (ActividadConTipo) -> Unit) {
        binding.tvNombreTipoActividad.text = actividad.nombreActividad
        binding.tvFechaActividad.text = "Fecha: ${actividad.fechaActividad}"
        binding.tvDescripcionGeneralActividad.text = "Descripción: ${actividad.descripcionGeneralActividad}"
        binding.cardActividad.setOnClickListener { onClickListener(actividad) }
    }
}