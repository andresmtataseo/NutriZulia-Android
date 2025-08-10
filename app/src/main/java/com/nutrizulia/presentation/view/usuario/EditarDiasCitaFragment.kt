package com.nutrizulia.presentation.view.usuario

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nutrizulia.databinding.FragmentEditarDiasCitaBinding
import com.nutrizulia.presentation.viewmodel.usuario.EditarDiasCitaViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditarDiasCitaFragment : Fragment() {

    private lateinit var binding: FragmentEditarDiasCitaBinding
    private val viewModel: EditarDiasCitaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarDiasCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exit.collect { exit ->
                if (exit) {
                    findNavController().popBackStack()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observar el valor actual de citas por día
            viewModel.maxAppointmentsPerDay.collect { maxAppointments ->
                binding.tiCitasPorDia.setText(maxAppointments.toString())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observar estado de carga
            viewModel.isLoading.collect { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observar mensajes de error
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    mostrarSnackbar(binding.root, it)
                    viewModel.clearError()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observar éxito al guardar
            viewModel.saveSuccess.collect { success ->
                if (success) {
                    mostrarSnackbar(binding.root, "Configuración guardada correctamente")
                    viewModel.clearSaveSuccess()
                }
            }
        }
    }

    private fun setupClickListeners() {
         binding.btnGuardar.setOnClickListener {
             val newValue = binding.tiCitasPorDia.text.toString().toIntOrNull()
             if (newValue != null && newValue > 0) {
                 viewModel.updateMaxAppointmentsPerDay(newValue)
                 viewModel.saveMaxAppointmentsPerDay()
             } else {
                 mostrarSnackbar(binding.root, "Por favor ingrese un valor válido")
             }
         }
    }
}