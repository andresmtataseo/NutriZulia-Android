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
import com.nutrizulia.util.Utils.obtenerTexto

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
        viewModel.crearUsuario()

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mostrarSnackbar(binding.root, mensaje)
        }

//        viewModel.error.observe(viewLifecycleOwner) { error ->
//            if (error) {
//                binding.tfCedula.error = " "
//                binding.tfContrasena.error = " "
//            } else {
//                binding.tfCedula.error = null
//                binding.tfContrasena.error = null
//            }
//
//        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) requireActivity().finish()
        }

        viewModel.autenticado.observe(viewLifecycleOwner) { autenticado ->
            if (autenticado) {
                val intent = Intent(requireContext(), PreCargarActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        binding.btnSalir.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnContinuar.setOnClickListener {
            viewModel.logearUsuario(binding.tfCedula.editText?.text.toString(), binding.tfContrasena.editText?.text.toString()
            )
        }

    }

}