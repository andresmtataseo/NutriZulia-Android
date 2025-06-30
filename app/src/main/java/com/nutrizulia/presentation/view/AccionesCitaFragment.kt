package com.nutrizulia.presentation.view

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.R
//import com.nutrizulia.presentation.viewmodel.AccionesCitaViewModel
import com.nutrizulia.databinding.FragmentAccionesCitaBinding
import com.nutrizulia.presentation.viewmodel.AccionesCitaViewModel
import com.nutrizulia.util.EstadoCita
import com.nutrizulia.util.Utils.calcularEdad
import com.nutrizulia.util.Utils.calcularEdadDetallada
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccionesCitaFragment : Fragment() {

    private val viewModel: AccionesCitaViewModel by viewModels()
    private lateinit var binding: FragmentAccionesCitaBinding
    private val args: AccionesCitaFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccionesCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate(args.idConsulta)
        setupObservers()
        setupListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {

        viewModel.mensaje.observe(viewLifecycleOwner) {
            mostrarSnackbar(requireView(), it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }

        viewModel.salir.observe(viewLifecycleOwner) {it ->
            if (it) findNavController().popBackStack()
        }

        viewModel.pacienteConCita.observe(viewLifecycleOwner) {

            binding.tvNombreCompletoPaciente.text = it.nombreCompleto
            binding.tvCedulaPaciente.text = "Cédula: ${it.cedulaPaciente}"
            val edad = calcularEdadDetallada(it.fechaNacimientoPaciente)
            binding.tvEdad.text = "Edad: ${edad.anios} años, ${edad.meses} meses y ${edad.dias} días"
            binding.tvFechaProgramada.text = "Fecha: ${it.fechaHoraProgramadaConsulta.toLocalDate()}"
            binding.tvHoraProgramda.text = "Hora: ${it.fechaHoraProgramadaConsulta.toLocalTime()}"
            binding.tvEstado.text = it.estadoConsulta.name

            val colorResId = when (it.estadoConsulta.name) {
                EstadoCita.PENDIENTE.descripcion,
                EstadoCita.REPROGRAMADA.descripcion -> R.color.color_cita_pendiente
                EstadoCita.COMPLETADA.descripcion -> R.color.color_cita_completada
                EstadoCita.CANCELADA.descripcion,
                EstadoCita.NO_ASISTIO.descripcion -> R.color.color_cita_cancelada
                else -> R.color.color_cita_pendiente
            }

            binding.tvEstado.setTextColor(ContextCompat.getColor(requireContext(), colorResId))

        }

    }

    fun setupListeners() {
        binding.cardViewRealizarConsulta.setOnClickListener { findNavController().navigate(AccionesCitaFragmentDirections.actionAccionesCitaFragmentToRegistrarConsultaFragment(args.idConsulta)) }
        binding.cardViewDetallesCita.setOnClickListener { findNavController().navigate(AccionesCitaFragmentDirections.actionAccionesCitaFragmentToVerCitaFragment(args.idConsulta)) }
        binding.cardViewModificarCita.setOnClickListener { findNavController().navigate(AccionesCitaFragmentDirections.actionAccionesCitaFragmentToReagendarCitaFragment(args.idConsulta)) }
        binding.cardViewCancelarCita.setOnClickListener {
            mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Desea cancelar la cita?",
                "Sí",
                "No",
                { viewModel.cancelarCita(args.idConsulta) },
                { },
                true
            )
        }
    }

}