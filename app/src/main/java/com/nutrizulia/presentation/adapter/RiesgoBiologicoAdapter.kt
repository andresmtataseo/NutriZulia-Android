package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.databinding.ItemRiesgoBiologicoBinding
import com.nutrizulia.domain.model.catalog.RiesgoBiologico

class RiesgoBiologicoAdapter (
    private var riesgosBiologicos: List<RiesgoBiologico>,
    private val onClickListener: (RiesgoBiologico) -> Unit,
    private var isReadOnly: Boolean = false
) : RecyclerView.Adapter<RiesgoBiologicoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiesgoBiologicoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riesgo_biologico, parent, false)
        return RiesgoBiologicoViewHolder(view)
    }

    override fun getItemCount(): Int = riesgosBiologicos.size

    override fun onBindViewHolder(holder: RiesgoBiologicoViewHolder, position: Int) {
        val riesgoBiologico = riesgosBiologicos[position]
        holder.bind(riesgoBiologico, onClickListener, isReadOnly)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRiesgosBiologicos(newRiegosBiologicos: List<RiesgoBiologico>) {
        this.riesgosBiologicos = newRiegosBiologicos
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setReadOnly(readOnly: Boolean) {
        this.isReadOnly = readOnly
        notifyDataSetChanged()
    }

}

class RiesgoBiologicoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = ItemRiesgoBiologicoBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(
        riesgoBiologico: RiesgoBiologico,
        onClickListener: (RiesgoBiologico) -> Unit,
        isReadOnly: Boolean
    ) {
        binding.tfRiesgoBiologico.editText?.setText(riesgoBiologico.nombre)
        if (isReadOnly) {
            binding.btnRemoverRiesgoBiologico.visibility = View.GONE
            binding.btnRemoverRiesgoBiologico.setOnClickListener(null)
        } else {
            binding.btnRemoverRiesgoBiologico.visibility = View.VISIBLE
            binding.btnRemoverRiesgoBiologico.setOnClickListener { onClickListener(riesgoBiologico) }
        }
    }
}