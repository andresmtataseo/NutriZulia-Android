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
import com.nutrizulia.data.remote.dto.collection.PacienteDto
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

    private fun configurarViewModel(): Unit {
        viewModel.onSyncStart = {
            binding.btnSincronizarPacientes.isEnabled = false
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizando)
        }
        
        viewModel.onSyncSuccess = { pacientes, mensaje ->
            binding.btnSincronizarPacientes.isEnabled = true
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizar_pacientes)
            mostrarMensajeExito(mensaje)
        }
        
        viewModel.onSyncConflictError = { mensaje, errors ->
            binding.btnSincronizarPacientes.isEnabled = true
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizar_pacientes)
            mostrarMensajeError(getString(R.string.error_sincronizacion_conflicto))
        }
        
        viewModel.onSyncBusinessError = { status, mensaje, errors ->
            binding.btnSincronizarPacientes.isEnabled = true
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizar_pacientes)
            mostrarMensajeError(getString(R.string.error_sincronizacion_negocio, mensaje))
        }
        
        viewModel.onSyncNetworkError = { code, mensaje ->
            binding.btnSincronizarPacientes.isEnabled = true
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizar_pacientes)
            mostrarMensajeError(getString(R.string.error_sincronizacion_red))
        }
        
        viewModel.onSyncUnknownError = { error ->
            binding.btnSincronizarPacientes.isEnabled = true
            binding.btnSincronizarPacientes.text = getString(R.string.sincronizar_pacientes)
            mostrarMensajeError(getString(R.string.error_sincronizacion_desconocido))
        }
    }

    private fun configurarClickListeners(): Unit {
        binding.btnSincronizarPacientes.setOnClickListener {
            viewModel.sincronizarPacientes()
        }
    }

    private fun mostrarMensajeExito(mensaje: String): Unit {
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.color_sync_success))
            .show()
    }
    
    private fun mostrarMensajeError(mensaje: String): Unit {
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.color_sync_error))
            .show()
    }
}