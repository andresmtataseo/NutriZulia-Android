package com.nutrizulia.presentation.view.representante

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentAccionesRepresentanteBinding
import com.nutrizulia.presentation.view.paciente.AccionesPacienteFragmentDirections
import com.nutrizulia.presentation.viewmodel.representante.AccionesRepresentanteViewModel
import com.nutrizulia.util.Utils.calcularEdadDetallada
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccionesRepresentanteFragment : Fragment() {

    private val viewModel: AccionesRepresentanteViewModel by viewModels()
    private lateinit var binding: FragmentAccionesRepresentanteBinding
    private val args: AccionesRepresentanteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccionesRepresentanteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.onCreate(args.representanteId)
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

        viewModel.representante.observe(viewLifecycleOwner) { representante ->
            binding.tvNombreCompleto.text = "${representante.nombres} ${representante.apellidos}"
            binding.tvCedula.text = "Cédula: ${representante.cedula}"
            binding.tvGenero.text = "Género: ${representante.genero}"
            binding.tvFechaNacimiento.text = "Fecha de nacimiento: ${representante.fechaNacimiento}"
            val edad = calcularEdadDetallada(representante.fechaNacimiento)
            binding.tvEdad.text = "Edad: ${edad.anios} años, ${edad.meses} meses y ${edad.dias} días"
        }
    }

    private fun setupListeners() {
        binding.cardViewInformacionPersonal.setOnClickListener {
//            findNavController().navigate(
//                AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentToRegistrarPacienteFragment(
//                    args.representanteId, false
//                )
//            )
        }

        binding.cardViewEditarInformacionPersonal.setOnClickListener {
//            findNavController().navigate(
//                AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentToRegistrarPacienteFragment(
//                    args.idPaciente
//                )
//            )
        }

        binding.cardViewPacientesRepresentados.setOnClickListener {
//            findNavController().navigate(
//                AccionesPacienteFragmentDirections.actionAccionesPacienteFragmentToHistoriaPacienteFragment(
//                    args.idPaciente
//                )
//            )
        }

    }

}