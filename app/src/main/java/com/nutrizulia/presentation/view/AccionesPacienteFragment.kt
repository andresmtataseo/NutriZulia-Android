package com.nutrizulia.presentation.view

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
//import com.nutrizulia.presentation.viewmodel.AccionesPacienteViewModel
import com.nutrizulia.databinding.FragmentAccionesPacienteBinding
import com.nutrizulia.util.Utils.calcularEdad
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccionesPacienteFragment : Fragment() {

//    private val viewModel: AccionesPacienteViewModel by viewModels()
    private lateinit var binding: FragmentAccionesPacienteBinding
    private val args: AccionesPacienteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccionesPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.obtenerPaciente(args.idPaciente)
//
//        viewModel.pacientes.observe(viewLifecycleOwner) { paciente ->
//            binding.tvNombreCompleto.text =
//                "${paciente.primerNombre} ${paciente.segundoNombre} ${paciente.primerApellido} ${paciente.segundoApellido}"
//            binding.tvCedula.text = "Cédula: ${paciente.cedula}"
//            binding.tvGenero.text = "Género: ${paciente.genero}"
//            binding.tvFechaNacimiento.text = "Fecha de nacimiento: ${paciente.fechaNacimiento}"
//            binding.tvEdad.text = "Edad: ${calcularEdad(paciente.fechaNacimiento).toString()}"
//        }
//
//        binding.cardViewInformacionPersonal.setOnClickListener {
//            findNavController().navigate(
//                AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentToVerPacienteFragment(
//                    args.idPaciente
//                )
//            )
//        }
//
//        binding.cardViewEditarInformacionPersonal.setOnClickListener {
//            findNavController().navigate(
//                AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentToEditarPacienteFragment(
//                    args.idPaciente
//                )
//            )
//        }
//
//        binding.cardViewHistoriaMedica.setOnClickListener {
//            findNavController().navigate(
//                AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentToHistoriaPacienteFragment(
//                    args.idPaciente
//                )
//            )
//        }

        //binding.cardViewResumenMedico.setOnClickListener { findNavController().navigate(AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentTo(args.idPaciente)) }

    }

}