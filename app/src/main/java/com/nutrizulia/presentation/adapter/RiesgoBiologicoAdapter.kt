package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.databinding.ItemRiesgoBiologicoBinding
import com.nutrizulia.domain.model.catalog.RiesgoBiologico

class RiesgoBiologicoAdapter (
    private var riesgosBiologicos: List<RiesgoBiologico>,
    private val onClickListener: (RiesgoBiologico) -> Unit,
    private var isReadOnly: Boolean = false,
    private var esPrimeraConsulta: Boolean = false,
    private var tieneDiagnosticoPrincipal: Boolean = false,
    private var diagnosticosHistoricos: List<RiesgoBiologico> = emptyList()
) : RecyclerView.Adapter<RiesgoBiologicoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiesgoBiologicoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riesgo_biologico, parent, false)
        return RiesgoBiologicoViewHolder(view)
    }

    override fun getItemCount(): Int = getAllDiagnosticos().size

    override fun onBindViewHolder(holder: RiesgoBiologicoViewHolder, position: Int) {
        val allDiagnosticos = getAllDiagnosticos()
        val (riesgoBiologico, esHistorico) = allDiagnosticos[position]
        
        // Solo los diagnósticos actuales pueden ser principales
        val posicionEnActuales = position - diagnosticosHistoricos.size
        val esPrincipal = esPrimeraConsulta && posicionEnActuales == 0 && !tieneDiagnosticoPrincipal && !esHistorico
        
        holder.bind(riesgoBiologico, onClickListener, isReadOnly || esHistorico, esPrincipal, esHistorico)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRiesgosBiologicos(nuevosRiesgosBiologicos: List<RiesgoBiologico>) {
        this.riesgosBiologicos = nuevosRiesgosBiologicos
        notifyDataSetChanged()
    }
    
    @SuppressLint("NotifyDataSetChanged")
    fun updateDiagnosticosHistoricos(nuevosHistoricos: List<RiesgoBiologico>) {
        this.diagnosticosHistoricos = nuevosHistoricos
        notifyDataSetChanged()
    }
    
    private fun getAllDiagnosticos(): List<Pair<RiesgoBiologico, Boolean>> {
        val historicos = diagnosticosHistoricos.map { it to true } // true = es histórico
        val actuales = riesgosBiologicos.map { it to false } // false = es actual
        return historicos + actuales
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setReadOnly(readOnly: Boolean) {
        this.isReadOnly = readOnly
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTieneDiagnosticoPrincipal(tienePrincipal: Boolean) {
        this.tieneDiagnosticoPrincipal = tienePrincipal
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setEsPrimeraConsulta(esPrimera: Boolean) {
        this.esPrimeraConsulta = esPrimera
        notifyDataSetChanged()
    }

}

class RiesgoBiologicoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = ItemRiesgoBiologicoBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(
        riesgoBiologico: RiesgoBiologico,
        onClickListener: (RiesgoBiologico) -> Unit,
        isReadOnly: Boolean,
        esPrincipal: Boolean = false,
        esHistorico: Boolean = false
    ) {
        val nombreConTipo = when {
            esPrincipal -> "${riesgoBiologico.nombre} (Principal)"
            esHistorico -> "${riesgoBiologico.nombre} (Histórico)"
            else -> riesgoBiologico.nombre
        }
        
        binding.tfRiesgoBiologico.editText?.setText(nombreConTipo)
        
        // Cambiar apariencia visual para diagnósticos históricos
        if (esHistorico) {
            binding.tiRiesgoBiologico.alpha = 0.7f
            binding.tfRiesgoBiologico.editText?.setTextColor(
                ContextCompat.getColor(itemView.context, android.R.color.darker_gray)
            )
        } else {
            binding.tiRiesgoBiologico.alpha = 1.0f
            binding.tfRiesgoBiologico.editText?.setTextColor(
                ContextCompat.getColor(itemView.context, android.R.color.black)
            )
        }
        
        if (isReadOnly) {
            binding.btnRemoverRiesgoBiologico.visibility = View.GONE
            binding.btnRemoverRiesgoBiologico.setOnClickListener(null)
        } else {
            binding.btnRemoverRiesgoBiologico.visibility = View.VISIBLE
            binding.btnRemoverRiesgoBiologico.setOnClickListener { onClickListener(riesgoBiologico) }
        }
    }
}