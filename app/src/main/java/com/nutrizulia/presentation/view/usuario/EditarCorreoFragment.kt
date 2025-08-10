package com.nutrizulia.presentation.view.usuario

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.databinding.FragmentEditarCorreoBinding
import com.nutrizulia.presentation.viewmodel.usuario.EditarCorreoViewModel
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditarCorreoFragment : Fragment() {

    private val viewModel: EditarCorreoViewModel by viewModels()
    private lateinit var binding: FragmentEditarCorreoBinding
    private val args: EditarCorreoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarCorreoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate(args.usuarioId)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            if (errores.isNotEmpty()) {
                for ((campo, mensaje) in errores) {
                    when (campo) {
                        "correo" -> binding.tfTCorreo.error = mensaje
                    }
                }
            } else {
                binding.tfTCorreo.error = null
            }
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            if (mensaje.isNotBlank()) mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack()
        }

        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            if (usuario == null) return@observe
            binding.tfTCorreo.editText?.setText(usuario.correo)
        }
    }
    private fun setupListeners() {
        binding.btnGuardar.setOnClickListener {
            viewModel.onSaveCorreoClicked(binding.tfTCorreo.editText?.text.toString())
        }

        binding.btnRestaurar.setOnClickListener {
            mostrarDialog(
                requireContext(),
                "Restaurar",
                "¿Desea restaurar los cambios?",
                "Restaurar",
                "No",
                { viewModel.onCreate(args.usuarioId) },
                {},
                true
            )
        }
    }
}