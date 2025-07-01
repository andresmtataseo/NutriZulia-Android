package com.nutrizulia.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nutrizulia.R
import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.databinding.ItemInstitucionBinding

class InstitucionAdapter(
    private var perfilesInstitucionales: List<PerfilInstitucional>,
    private val onClickListener: (PerfilInstitucional) -> Unit
) : RecyclerView.Adapter<PerfilInstitucionalViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerfilInstitucionalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_institucion, parent, false)
        return PerfilInstitucionalViewHolder(view)
    }

    override fun getItemCount(): Int = perfilesInstitucionales.size

    override fun onBindViewHolder(holder: PerfilInstitucionalViewHolder, position: Int) {
        val perfilInstitucional = perfilesInstitucionales[position]
        holder.bind(perfilInstitucional, position == selectedPosition, onClickListener)

        holder.itemView.setOnClickListener {
            // Notificamos al item previamente seleccionado para que se "desmarque"
            notifyItemChanged(selectedPosition)
            // Actualizamos la nueva posición seleccionada
            selectedPosition = holder.adapterPosition
            // Notificamos al nuevo item seleccionado para que se "marque"
            notifyItemChanged(selectedPosition)

            // Ejecutamos la acción que nos pasó la Activity (llamar al ViewModel)
            onClickListener(perfilInstitucional)
        }
    }

    fun updatePerfilesInstitucionales(newPerfiles: List<PerfilInstitucional>) {
        val diffCallback = PerfilDiffCallback(this.perfilesInstitucionales, newPerfiles)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.perfilesInstitucionales = newPerfiles
        diffResult.dispatchUpdatesTo(this)
    }
}

class PerfilInstitucionalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemInstitucionBinding.bind(itemView)

    @SuppressLint("SetTextI18n", "ResourceType")
    fun bind(
        perfil: PerfilInstitucional,
        isSelected: Boolean,
        onClickListener: (PerfilInstitucional) -> Unit
    ) {
        binding.tvNombreCompleto.text = perfil.institucionNombre
        binding.tvCargo.text = "Cargo: ${perfil.rolNombre}"
        binding.tvTipoInstitucion.text = "Tipo: ${perfil.tipoInstitucionNombre}"
        binding.tvMunicipioSanitario.text = "Municipio: ${perfil.municipioNombre}"
        binding.cardInstitucion.isChecked = isSelected

    }
}


class PerfilDiffCallback(
    private val oldList: List<PerfilInstitucional>,
    private val newList: List<PerfilInstitucional>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].usuarioInstitucionId == newList[newItemPosition].usuarioInstitucionId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}