package com.nutrizulia.presentation.view.usuario

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nutrizulia.databinding.FragmentEditarClaveBinding
import com.nutrizulia.presentation.viewmodel.usuario.EditarClaveViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditarClaveFragment : Fragment() {

    private val viewModel: EditarClaveViewModel by viewModels()
    private lateinit var binding: FragmentEditarClaveBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarClaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnGuardar.setOnClickListener {
            val claveActual = binding.tiClave.text.toString()
            val claveNueva = binding.tiClaveNueva.text.toString()
            val claveNuevaConfirmacion = binding.tiClaveNuevaConfirmacion.text.toString()
            
            viewModel.cambiarClave(claveActual, claveNueva, claveNuevaConfirmacion)
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            if (errores.isNotEmpty()) {
                for ((campo, mensaje) in errores) {
                    when (campo) {
                        "contraseña" -> binding.tfClave.error = mensaje
                        "contraseñaNueva" -> binding.tfClaveNueva.error = mensaje
                        "contraseñaNuevaConfirmacion" -> binding.tfClaveNuevaConfirmacion.error = mensaje
                    }
                }
            } else {
                binding.tfClave.error = null
                binding.tfClaveNueva.error = null
                binding.tfClaveNuevaConfirmacion.error = null
            }
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            if (mensaje.isNotBlank()) mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack()
        }

    }

}