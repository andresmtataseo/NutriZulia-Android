package com.nutrizulia.presentation.view.usuario

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.databinding.FragmentEditarTelefonoBinding
import com.nutrizulia.presentation.viewmodel.usuario.EditarTelefonoViewModel
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditarTelefonoFragment : Fragment() {

    private val viewModel: EditarTelefonoViewModel by viewModels()
    private lateinit var binding: FragmentEditarTelefonoBinding
    private val args: EditarTelefonoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarTelefonoBinding.inflate(inflater, container, false)
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
                        "telefono" -> binding.tfTelefono.error = mensaje
                    }
                }
            } else {
                binding.tfTelefono.error = null
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

            val telefono = usuario.telefono ?: ""
            val prefijo: String
            val numero: String

            if (telefono.contains("-")) {
                val partes = telefono.split("-")
                prefijo = partes.getOrNull(0) ?: ""
                numero = partes.getOrNull(1) ?: ""
            } else {
                if (telefono.length >= 4) {
                    prefijo = telefono.substring(0, 4)
                    numero = telefono.substring(4)
                } else {
                    prefijo = telefono
                    numero = ""
                }
            }

            (binding.tfPrefijo.editText as? AutoCompleteTextView)?.setText(prefijo, false)
            binding.tfTelefono.editText?.setText(numero)
        }
    }

    private fun setupListeners() {

        binding.btnGuardar.setOnClickListener {
            val prefijo = binding.tfPrefijo.editText?.text.toString()
            val numero = binding.tfTelefono.editText?.text.toString()
            val numeroCompleto = "$prefijo-$numero"
            viewModel.onSaveTelefonoClicked(numeroCompleto)
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