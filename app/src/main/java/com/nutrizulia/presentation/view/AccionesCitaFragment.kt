package com.nutrizulia.presentation.view

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.R
//import com.nutrizulia.presentation.viewmodel.AccionesCitaViewModel
import com.nutrizulia.databinding.FragmentAccionesCitaBinding
import com.nutrizulia.util.EstadoCita
import com.nutrizulia.util.Utils.calcularEdad
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccionesCitaFragment : Fragment() {

//    private val viewModel: AccionesCitaViewModel by viewModels()
    private lateinit var binding: FragmentAccionesCitaBinding
    private val args: AccionesCitaFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccionesCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.obtenerPaciente(args.idCita)
//
//        viewModel.citaConPaciente.observe(viewLifecycleOwner) {
//            val paciente = it.paciente
//            val cita = it.cita
//
//            binding.tvNombreCompletoPaciente.text = "${paciente.primerNombre} ${paciente.segundoNombre} ${paciente.primerApellido} ${paciente.segundoApellido}"
//            binding.tvCedulaPaciente.text = "Cédula: ${paciente.cedula}"
//            binding.tvEdad.text = "Edad: ${calcularEdad(paciente.fechaNacimiento)} años"
//            binding.tvFechaProgramada.text = "Fecha: ${cita.fechaProgramada}"
//            binding.tvHoraProgramda.text = "Hora: ${cita.horaProgramada}"
//            binding.tvEstado.text = cita.estado
//
//            val colorResId = when (cita.estado) {
//                EstadoCita.PENDIENTE.descripcion,
//                EstadoCita.REPROGRAMADA.descripcion -> R.color.color_cita_pendiente
//                EstadoCita.COMPLETADA.descripcion -> R.color.color_cita_completada
//                EstadoCita.CANCELADA.descripcion,
//                EstadoCita.NO_ASISTIO.descripcion -> R.color.color_cita_cancelada
//                else -> R.color.color_cita_pendiente
//            }
//
//            binding.tvEstado.setTextColor(ContextCompat.getColor(requireContext(), colorResId))
//
//            binding.cardViewRealizarConsulta.setOnClickListener { findNavController().navigate(AccionesCitaFragmentDirections.actionAccionesCitaFragmentToRegistrarConsultaFragment(cita.id)) }
//            binding.cardViewDetallesCita.setOnClickListener { findNavController().navigate(AccionesCitaFragmentDirections.actionAccionesCitaFragmentToVerCitaFragment(cita.id)) }
//            binding.cardViewModificarCita.setOnClickListener { findNavController().navigate(AccionesCitaFragmentDirections.actionAccionesCitaFragmentToReagendarCitaFragment(cita.id)) }
//            binding.cardViewCancelarCita.setOnClickListener {
//                mostrarDialog(
//                    requireContext(),
//                    "Advertencia",
//                    "¿Desea cancelar la cita?",
//                    "Sí",
//                    "No",
//                    { viewModel.cancelarCita(cita.id) },
//                    { },
//                    true
//                )
//            }
//
//        }
//
//        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(requireView(), it) }
//        viewModel.isLoading.observe(viewLifecycleOwner) { binding.progress.visibility = if (it) View.VISIBLE else View.GONE }
//        viewModel.salir.observe(viewLifecycleOwner) { if (it) findNavController().popBackStack() }

    }

}