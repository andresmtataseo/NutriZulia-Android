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
import com.nutrizulia.domain.model.Cita
import com.nutrizulia.util.EstadoCita

class CitaAdapter(
    private var citas: List<Cita>,
    private val onClickListener: (Cita) -> Unit
) : RecyclerView.Adapter<CitaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        // Inflar el layout usando LayoutInflater
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun getItemCount(): Int = citas.size

    // La anotación RequiresApi(Build.VERSION_CODES.O) puede no ser necesaria aquí
    // si el código dentro de onBindViewHolder no requiere API O.
    // El método bind sí la tiene, así que se mantiene.
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]
        holder.bind(cita, onClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCitas(newCitas: List<Cita>) {
        this.citas = newCitas
        // Considerar usar DiffUtil para actualizaciones más eficientes en lugar de notifyDataSetChanged
        notifyDataSetChanged()
    }
}

class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = ItemCitaBinding.bind(itemView)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun bind(cita: Cita, onClickListener: (Cita) -> Unit) {
        binding.apply {

            tvFechaProgramada.text = "Fecha: ${cita.fechaProgramada}"
            tvHoraProgramda.text = "Hora: ${cita.horaProgramada}"
            tvEstado.text = "Estado: ${cita.estado}"

            val colorResId = when (cita.estado) {
                EstadoCita.PENDIENTE.descripcion -> R.color.color_cita_pendiente
                EstadoCita.COMPLETADA.descripcion -> R.color.color_cita_completada
                EstadoCita.CANCELADA.descripcion -> R.color.color_cita_cancelada
                EstadoCita.REPROGRAMADA.descripcion -> R.color.color_cita_pendiente
                EstadoCita.NO_ASISTIO.descripcion -> R.color.color_cita_cancelada
                else -> {
                    R.color.color_cita_pendiente
                }
            }

            cardCita.setCardBackgroundColor(
                ContextCompat.getColor(itemView.context, colorResId)
            )

            cardCita.setOnClickListener { onClickListener(cita) }
        }
    }
}