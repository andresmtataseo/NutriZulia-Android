package com.nutrizulia.presentation.view

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.navArgs
import com.nutrizulia.databinding.FragmentAccionesConsultaBinding
//import com.nutrizulia.presentation.viewmodel.AccionesConsultaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccionesConsultaFragment : Fragment() {

//    private val viewModel: AccionesConsultaViewModel by viewModels()
    private lateinit var binding: FragmentAccionesConsultaBinding
    private val args: AccionesConsultaFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccionesConsultaBinding.inflate(inflater, container, false)
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
//            viewModel.obtenerConsulta(args.idCita)
//
//            viewModel.consulta.observe(viewLifecycleOwner) { consulta ->
//                if (consulta != null) {
//                    binding.cardViewDetallesConsulta.setOnClickListener { findNavController().navigate(AccionesConsultaFragmentDirections.actionAccionesConsultaFragmentToVerConsultaFragment(consulta.id)) }
//                    binding.cardViewModificarConsulta.setOnClickListener { findNavController().navigate(AccionesConsultaFragmentDirections.actionAccionesConsultaFragmentToEditarConsultaFragment(consulta.id)) }
//                    binding.cardViewBorrarConsulta.setOnClickListener {
//                mostrarDialog(
//                    requireContext(),
//                    "Advertencia",
//                    "¿Desea borrar la consulta?",
//                    "Sí",
//                    "No",
//                    { viewModel.borrarConsulta(consulta.id) },
//                    { },
//                    true
//                )
//                    }
//                }
//            }
//
//            binding.cardViewDetallesCita.setOnClickListener { findNavController().navigate(AccionesConsultaFragmentDirections.actionAccionesConsultaFragmentToVerCitaFragment(cita.id)) }
//
//        }

//        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(requireView(), it) }
//        viewModel.isLoading.observe(viewLifecycleOwner) { binding.progress.visibility = if (it) View.VISIBLE else View.GONE }
//        viewModel.salir.observe(viewLifecycleOwner) { if (it) findNavController().popBackStack() }

    }

}