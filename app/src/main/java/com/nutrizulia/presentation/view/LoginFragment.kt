package com.nutrizulia.presentation.view

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentLoginBinding
import com.nutrizulia.presentation.viewmodel.LoginViewModel
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

        // Seleccionar primer ítem del array como valor por defecto
        val tipoCedulaArray = resources.getStringArray(R.array.tiposCedulas)
        binding.autoTipoCedula.setText(tipoCedulaArray[0], false)

        // Limpia error al modificar texto en cédula
        binding.tfCedula.editText?.doOnTextChanged { _, _, _, _ ->
            binding.tfTipoCedula.error = null
            binding.tfCedula.error = null
        }

        binding.tfTipoCedula.editText?.doOnTextChanged { _, _, _, _ ->
            binding.tfTipoCedula.error = null
            binding.tfCedula.error = null
        }

        // Limpia error al modificar texto en contraseña
        binding.tfContrasena.editText?.doOnTextChanged { _, _, _, _ ->
            binding.tfContrasena.error = null
        }

        // Observa estado de carga para mostrar/hide progress bar y habilitar botones
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
            binding.btnContinuar.isEnabled = !isLoading
        }

        // Observa resultado de autenticación
        viewModel.signInResult.observe(viewLifecycleOwner) { result ->
            // Limpia errores previos
            binding.tfTipoCedula.error = null
            binding.tfCedula.error = null
            binding.tfContrasena.error = null

            result.onSuccess {
                // Login exitoso, navega a siguiente pantalla y limpia back stack
                val intent = Intent(requireContext(), PreCargarActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            result.onFailure { error ->
                mostrarSnackbar(binding.root, error.message ?: "Error desconocido")

                // Opcional: si error es por campos vacíos, muestra error en inputs
                if (error.message == "Completa los campos.") {
                    if (binding.tfCedula.editText?.text.isNullOrBlank()) {
                        binding.tfCedula.error = "Requerido"
                    }
                    if (binding.tfContrasena.editText?.text.isNullOrBlank()) {
                        binding.tfContrasena.error = "Requerido"
                    }
                } else {
                    binding.tfTipoCedula.error = " "
                    binding.tfCedula.error = "Verifica la cédula"
                    binding.tfContrasena.error = "Verifica la contraseña"
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
}
