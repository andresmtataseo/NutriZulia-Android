package com.nutrizulia.presentation.view

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nutrizulia.presentation.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

import com.nutrizulia.databinding.FragmentLoginBinding
import com.nutrizulia.util.Utils.mostrarSnackbar

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

        viewModel.signInResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { signIn ->
                val intent = Intent(requireContext(), PreCargarActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            result.onFailure { error ->
                mostrarSnackbar(binding.root, error.message ?: "Error desconocido")
                binding.tfCedula.error = " "
                binding.tfContrasena.error = " "
            }
        }

        binding.btnSalir.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnContinuar.setOnClickListener {
            val cedula = binding.tfCedula.editText?.text.toString()
            val clave = binding.tfContrasena.editText?.text.toString()
            viewModel.logearUsuario(cedula, clave)
        }
    }
}