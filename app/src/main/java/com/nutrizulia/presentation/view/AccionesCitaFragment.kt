package com.nutrizulia.presentation.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.databinding.FragmentAccionesCitaBinding
import com.nutrizulia.presentation.viewmodel.AccionesCitaViewModel
import com.nutrizulia.util.Utils.calcularEdadDetallada
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class AccionesCitaFragment : Fragment() {

    private val viewModel: AccionesCitaViewModel by viewModels()
    private lateinit var binding: FragmentAccionesCitaBinding
    private val args: AccionesCitaFragmentArgs by navArgs()
    private var pacienteId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccionesCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.onCreate(args.idConsulta)
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

        viewModel.pacienteConCita.observe(viewLifecycleOwner) {
            // Asigna el valor a la variable de la clase.
            this.pacienteId = it.pacienteId

            binding.tvNombreCompletoPaciente.text = it.nombreCompleto
            binding.tvCedulaPaciente.text = "Cédula: ${it.cedulaPaciente}"
            val edad = calcularEdadDetallada(it.fechaNacimientoPaciente)
            binding.tvEdad.text = "Edad: ${edad.anios} años, ${edad.meses} meses y ${edad.dias} días"
            binding.tvFechaProgramada.text = "Fecha programada: ${it.fechaHoraProgramadaConsulta.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
            binding.tvHoraProgramda.text = "Hora programada: ${it.fechaHoraProgramadaConsulta.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))}"
            binding.tvEstado.text = it.estadoConsulta.displayValue

            val colorResId = when (it.estadoConsulta) { // Compara con el enum directamente
                Estado.PENDIENTE,
                Estado.REPROGRAMADA -> R.color.color_cita_pendiente
                Estado.COMPLETADA,
                Estado.SIN_PREVIA_CITA -> R.color.color_cita_completada
                Estado.CANCELADA,
                Estado.NO_ASISTIO -> R.color.color_cita_cancelada
            }
            binding.tvEstado.setTextColor(ContextCompat.getColor(requireContext(), colorResId))
        }
    }

    fun setupListeners() {
        binding.cardViewRealizarConsulta.setOnClickListener { findNavController().navigate(AccionesCitaFragmentDirections.actionAccionesCitaFragmentToRegistrarConsultaFragment(args.idConsulta)) }

        binding.cardViewDetallesCita.setOnClickListener {
            pacienteId?.let { id ->
                val action = AccionesCitaFragmentDirections.actionAccionesCitaFragmentToRegistrarCitaFragment(
                    idPaciente = id,
                    idConsulta = args.idConsulta,
                    isEditable = false
                )
                findNavController().navigate(action)
            } ?: run {
                mostrarSnackbar(requireView(), "Cargando datos, por favor espere...")
            }
        }

        binding.cardViewModificarCita.setOnClickListener {
                pacienteId?.let { id ->
                val action = AccionesCitaFragmentDirections.actionAccionesCitaFragmentToRegistrarCitaFragment(
                    idPaciente = id,
                    idConsulta = args.idConsulta
                )
                findNavController().navigate(action)
            } ?: run {
                mostrarSnackbar(requireView(), "Cargando datos, por favor espere...")
            }
        }

        binding.cardViewCancelarCita.setOnClickListener {
            mostrarDialog(
                requireContext(),
                "Cancelar cita",
                "¿Está seguro que desea cancelar la cita?",
                "Cancelar",
                "No",
                { viewModel.cancelarCita(args.idConsulta) },
                { },
                true
            )
        }
    }
}