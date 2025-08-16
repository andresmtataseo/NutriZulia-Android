package com.nutrizulia.presentation.view.paciente

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
//import com.nutrizulia.presentation.viewmodel.paciente.AccionesPacienteViewModel
import com.nutrizulia.databinding.FragmentAccionesPacienteBinding
import com.nutrizulia.presentation.viewmodel.paciente.AccionesPacienteViewModel
import com.nutrizulia.util.Utils.calcularEdadDetallada
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccionesPacienteFragment : Fragment() {

    private val viewModel: AccionesPacienteViewModel by viewModels()
    private lateinit var binding: FragmentAccionesPacienteBinding
    private val args: AccionesPacienteFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccionesPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.onCreate(args.idPaciente)
        setupListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) {
            mostrarSnackbar(requireView(), it)
        }

        viewModel.salir.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }

        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            binding.tvNombreCompleto.text = "${paciente.nombres} ${paciente.apellidos}"
            binding.tvCedula.text = "Cédula: ${paciente.cedula}"
            binding.tvGenero.text = "Género: ${paciente.genero}"
            binding.tvFechaNacimiento.text = "Fecha de nacimiento: ${paciente.fechaNacimiento}"
            val edad = calcularEdadDetallada(paciente.fechaNacimiento)
            binding.tvEdad.text = "Edad: ${edad.anios} años, ${edad.meses} meses y ${edad.dias} días"
        }
    }

    private fun setupListeners() {
        binding.cardViewInformacionPersonal.setOnClickListener {
            findNavController().navigate(
                AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentToRegistrarPacienteFragment(
                    args.idPaciente, false
                )
            )
        }

        binding.cardViewEditarInformacionPersonal.setOnClickListener {
            findNavController().navigate(
                AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentToRegistrarPacienteFragment(
                    args.idPaciente
                )
            )
        }

        binding.cardViewHistoriaMedica.setOnClickListener {
            findNavController().navigate(
                AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentToHistoriaPacienteFragment(
                    args.idPaciente
                )
            )
        }

//        binding.cardViewResumenMedico.setOnClickListener {
//            findNavController().navigate(AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentTo(args.idPaciente))
//        }

    }

}