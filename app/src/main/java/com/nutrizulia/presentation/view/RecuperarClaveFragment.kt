package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentRecuperarClaveBinding
import com.nutrizulia.presentation.viewmodel.RecuperarClaveViewModel
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecuperarClaveFragment : Fragment() {

    private val viewModel: RecuperarClaveViewModel by viewModels()
    private lateinit var binding: FragmentRecuperarClaveBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecuperarClaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        // Configurar botones
        binding.btnRecuperar.setOnClickListener {
            val tipoCedula = binding.autoTipoCedula.text.toString()
            val cedula = binding.tfCedula.editText?.text.toString() ?: ""
            viewModel.recuperarClave(tipoCedula, cedula)
        }

        binding.btnNoPuedesRecuperar.setOnClickListener {
            showContactAdminDialog()
        }

        binding.btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        // Seleccionar primer ítem del array como valor por defecto
        val tipoCedulaArray = resources.getStringArray(R.array.tiposCedulas)
        binding.autoTipoCedula.setText(tipoCedulaArray[0], false)

        // Limpiar errores al modificar texto
        binding.tfCedula.editText?.doOnTextChanged { _, _, _, _ ->
            viewModel.clearErrors()
        }

        binding.autoTipoCedula.doOnTextChanged { _, _, _, _ ->
            viewModel.clearErrors()
        }
    }

    private fun setupObservers() {
        // Observar estado de carga
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
            binding.btnRecuperar.isEnabled = !isLoading
            binding.btnVolver.isEnabled = !isLoading
        }

        // Observar errores de validación
        viewModel.validationErrors.observe(viewLifecycleOwner) { errors ->
            // Limpiar errores previos
            binding.tfTipoCedula.error = null
            binding.tfCedula.error = null

            // Mostrar errores específicos
            errors["tipoCedula"]?.let { error ->
                binding.tfTipoCedula.error = error
            }
            errors["cedula"]?.let { error ->
                binding.tfCedula.error = error
            }

            // Mostrar Snackbar si hay errores de validación
            if (errors.isNotEmpty()) {
                mostrarSnackbar(binding.root, getString(R.string.corrija_errores_formulario))
            }
        }

        // Observar mensaje de éxito
        viewModel.successMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                showSuccessDialog(it)
                viewModel.clearSuccessMessage()
            }
        }

        // Observar mensaje de error
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                mostrarSnackbar(binding.root, it)
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun showSuccessDialog(message: String) {
        mostrarDialog(
            requireContext(),
            getString(R.string.recuperacion_exitosa),
            message,
            positiveButtonText = getString(R.string.entendido),
            onPositiveClick = {
                findNavController().popBackStack()
            })
    }

    private fun showContactAdminDialog() {
        mostrarDialog(
            requireContext(),
            getString(R.string.contacto_admin_titulo),
            getString(R.string.contacto_admin_mensaje),
            positiveButtonText = getString(R.string.entendido)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearMessages()
    }
}