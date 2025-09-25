package com.nutrizulia.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.databinding.ItemDiagnosticoBinding
import com.nutrizulia.presentation.model.DiagnosticoItem
import java.time.format.DateTimeFormatter

class DiagnosticoAdapter : ListAdapter<DiagnosticoItem, DiagnosticoAdapter.DiagnosticoViewHolder>(DiagnosticoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosticoViewHolder {
        val binding = ItemDiagnosticoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiagnosticoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiagnosticoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiagnosticoViewHolder(
        private val binding: ItemDiagnosticoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DiagnosticoItem) {
            binding.tvDiagnostico.text = item.texto
            binding.tvFechaDiagnostico.text = "Fecha: ${item.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
        }
    }

    private class DiagnosticoDiffCallback : DiffUtil.ItemCallback<DiagnosticoItem>() {
        override fun areItemsTheSame(oldItem: DiagnosticoItem, newItem: DiagnosticoItem): Boolean {
            return oldItem.texto == newItem.texto && oldItem.fecha == newItem.fecha
        }

        override fun areContentsTheSame(oldItem: DiagnosticoItem, newItem: DiagnosticoItem): Boolean {
            return oldItem == newItem
        }
    }
}