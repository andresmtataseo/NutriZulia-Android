package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentInicioBinding
import com.nutrizulia.presentation.viewmodel.InicioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InicioFragment : Fragment() {

    private val viewModel: InicioViewModel by viewModels()
    private lateinit var binding: FragmentInicioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarViewModel()
        configurarClickListeners()
    }

    private fun configurarViewModel() {
        viewModel.onSyncStart = {
            binding.btnSincronizarPacientes.isEnabled = false
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizando)
        }
        
        viewModel.onSyncSuccess = { successCount, totalOperations, mensaje ->
            binding.btnSincronizarPacientes.isEnabled = true
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizar_pacientes)
            val mensajeCompleto = "$mensaje ($successCount/$totalOperations operaciones completadas)"
            mostrarMensajeExito(mensajeCompleto)
        }
        
        viewModel.onSyncPartialSuccess = { successCount, totalOperations, failedBatches, mensaje ->
            binding.btnSincronizarPacientes.isEnabled = true
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizar_pacientes)
            val mensajeCompleto = "$mensaje ($successCount/$totalOperations operaciones). Lotes fallidos: ${failedBatches.joinToString(", ")}"
            mostrarMensajeAdvertencia(mensajeCompleto)
        }
        
        viewModel.onSyncError = { mensaje, details ->
            binding.btnSincronizarPacientes.isEnabled = true
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizar_pacientes)
            val mensajeCompleto = if (details != null) "$mensaje. $details" else mensaje
            mostrarMensajeError(mensajeCompleto)
        }
    }

    private fun configurarClickListeners() {
        binding.btnSincronizarPacientes.setOnClickListener {
            viewModel.sincronizarPacientes()
        }
    }

    private fun mostrarMensajeExito(mensaje: String) {
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.color_sync_success))
            .show()
    }
    
    private fun mostrarMensajeAdvertencia(mensaje: String) {
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light))
            .show()
    }
    
    private fun mostrarMensajeError(mensaje: String) {
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.color_sync_error))
            .show()
    }
}