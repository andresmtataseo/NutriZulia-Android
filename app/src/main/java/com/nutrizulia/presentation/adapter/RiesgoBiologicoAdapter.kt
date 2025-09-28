package com.nutrizulia.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.databinding.ItemRiesgoBiologicoBinding
import com.nutrizulia.presentation.viewmodel.consulta.DiagnosticoParaUI

class RiesgoBiologicoAdapter(
    private val onEliminarClick: (DiagnosticoParaUI) -> Unit,
    private var isReadOnlyMode: Boolean = false
) : ListAdapter<DiagnosticoParaUI, RiesgoBiologicoAdapter.RiesgoBiologicoViewHolder>(DiffCallback) {

    /**
     * Actualiza el modo de solo lectura y refresca la lista
     */
    fun updateReadOnlyMode(readOnlyMode: Boolean) {
        if (isReadOnlyMode != readOnlyMode) {
            isReadOnlyMode = readOnlyMode
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiesgoBiologicoViewHolder {
        val binding = ItemRiesgoBiologicoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RiesgoBiologicoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RiesgoBiologicoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RiesgoBiologicoViewHolder(
        private val binding: ItemRiesgoBiologicoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(diagnostico: DiagnosticoParaUI) {
            binding.apply {
                // Mostrar el nombre completo (incluye enfermedad si existe)
                tiRiesgoBiologico.setText(diagnostico.nombreCompleto)
                
                // Ocultar el botón de remover en modo de solo lectura
                btnRemoverRiesgoBiologico.visibility = if (isReadOnlyMode) View.GONE else View.VISIBLE
                
                btnRemoverRiesgoBiologico.setOnClickListener {
                    onEliminarClick(diagnostico)
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DiagnosticoParaUI>() {
            override fun areItemsTheSame(oldItem: DiagnosticoParaUI, newItem: DiagnosticoParaUI): Boolean {
                return oldItem.riesgoBiologico.id == newItem.riesgoBiologico.id && 
                       oldItem.enfermedad?.id == newItem.enfermedad?.id
            }

            override fun areContentsTheSame(oldItem: DiagnosticoParaUI, newItem: DiagnosticoParaUI): Boolean {
                return oldItem == newItem
            }
        }
    }
}