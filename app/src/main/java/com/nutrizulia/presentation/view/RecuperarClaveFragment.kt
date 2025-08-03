package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentRecuperarClaveBinding
import com.nutrizulia.presentation.viewmodel.RecuperarClaveViewModel
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

        binding.btnRecuperar.setOnClickListener {
            val tipoCedula = binding.tfTipoCedula.editText?.text.toString()
            val cedula = binding.tfCedula.editText?.text.toString()
            val cedulaCompleta = "$tipoCedula-$cedula"
            viewModel.recuperarClave(cedulaCompleta)
        }

        binding.btnNoPuedesRecuperar.setOnClickListener {
            // mostrar mensaje para que contacte al administrador
        }

        binding.btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }

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

        // Observa estado de carga para mostrar/hide progress bar y habilitar botones
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
            binding.btnRecuperar.isEnabled = !isLoading
        }
    }

}