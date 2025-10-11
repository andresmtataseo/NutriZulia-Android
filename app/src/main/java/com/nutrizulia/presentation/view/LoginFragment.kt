package com.nutrizulia.presentation.view

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentLoginBinding
import com.nutrizulia.presentation.viewmodel.LoginViewModel
import com.nutrizulia.presentation.viewmodel.SignInError
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOlvidasteContrasena.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recuperarClaveFragment)
        }

        binding.btnAyuda.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_ayudaFragment2)
        }

        // Seleccionar primer ítem del array como valor por defecto
        val tipoCedulaArray = resources.getStringArray(R.array.tiposCedulas)
        binding.autoTipoCedula.setText(tipoCedulaArray[0], false)

        // Limpia error al modificar texto en cédula
        binding.tfCedula.editText?.doOnTextChanged { _, _, _, _ ->
            clearInputErrors()
        }

        binding.tfTipoCedula.editText?.doOnTextChanged { _, _, _, _ ->
            clearInputErrors()
        }

        // Limpia error al modificar texto en contraseña
        binding.tfContrasena.editText?.doOnTextChanged { _, _, _, _ ->
            clearInputErrors()
        }

        // Observa estado de carga para mostrar/hide progress bar y habilitar botones
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
            binding.btnContinuar.isEnabled = !isLoading
        }

        // Observa resultado de autenticación exitosa
        viewModel.signInResult.observe(viewLifecycleOwner) { result ->
            // Limpia errores previos
            clearInputErrors()

            result.onSuccess {
                // Login exitoso, navega a siguiente pantalla y limpia back stack
                val intent = Intent(requireContext(), PreCargarActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        // Observa errores diferenciados
        viewModel.signInError.observe(viewLifecycleOwner) { error ->
            when (error) {
                is SignInError.InvalidInput -> {
                    mostrarSnackbar(binding.root, error.message)
                    // Marcar inputs específicos si están vacíos
                    if (binding.tfCedula.editText?.text.isNullOrBlank()) {
                        binding.tfCedula.error = "Requerido"
                    }
                    if (binding.tfContrasena.editText?.text.isNullOrBlank()) {
                        binding.tfContrasena.error = "Requerido"
                    }
                }
                is SignInError.Forbidden -> {
                    // Errores 403: marcar campos y mostrar mensaje del servidor
                    setCredentialErrors()
                    mostrarSnackbar(binding.root, error.message)
                }
                is SignInError.Other -> {
                    // Otros errores: solo mostrar mensaje, sin marcar campos
                    clearInputErrors()
                    mostrarSnackbar(binding.root, error.message)
                }
            }
        }

        binding.btnSalir.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnContinuar.setOnClickListener {
            val tipoCedula = binding.tfTipoCedula.editText?.text.toString()
            val cedula = binding.tfCedula.editText?.text.toString()
            val cedulaCompleta = "$tipoCedula-$cedula"
            val clave = binding.tfContrasena.editText?.text.toString()
            viewModel.logearUsuario(cedulaCompleta, clave)
        }
    }

    private fun clearInputErrors() {
        binding.tfTipoCedula.error = null
        binding.tfCedula.error = null
        binding.tfContrasena.error = null
    }

    private fun setCredentialErrors() {
        binding.tfTipoCedula.error = " "
        binding.tfCedula.error = "Verifica la cédula"
        binding.tfContrasena.error = "Verifica la contraseña"
    }
}
