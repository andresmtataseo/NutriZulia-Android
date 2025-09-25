package com.nutrizulia.presentation.view.consulta

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.databinding.FragmentAccionesConsultaBinding
import com.nutrizulia.presentation.viewmodel.consulta.AccionesConsultaViewModel
import com.nutrizulia.presentation.viewmodel.consulta.ConsultaSharedViewModel
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class AccionesConsultaFragment : Fragment() {

    private val viewModel: AccionesConsultaViewModel by viewModels()
    private lateinit var binding: FragmentAccionesConsultaBinding
    private val args: AccionesConsultaFragmentArgs by navArgs()
    private var pacienteId: String? = null
    private var isHistoria: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccionesConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isHistoria = args.isHistoria
        if (isHistoria) binding.line.visibility = View.GONE
        val timestamp = System.currentTimeMillis()
        android.util.Log.d("NavFlow", "AccionesConsultaFragment: onViewCreated con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=${args.idConsulta}")
        
        // Obtener referencia al SharedViewModel y guardar el valor de isHistoria
        val sharedViewModel: ConsultaSharedViewModel by activityViewModels()
        sharedViewModel.setIsHistoria(isHistoria)
        
        // Sincronizar con el SharedViewModel en caso de que haya cambiado en otro fragmento
        sharedViewModel.isHistoria.observe(viewLifecycleOwner) { historiaValue ->
            if (isHistoria != historiaValue) {
                isHistoria = historiaValue
                android.util.Log.d("NavFlow", "AccionesConsultaFragment: isHistoria actualizado desde SharedViewModel: $isHistoria")
            }
        }
        
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
            Utils.mostrarSnackbar(requireView(), it)
        }

        viewModel.salir.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }

        viewModel.pacienteConCita.observe(viewLifecycleOwner) {
            // Asigna el valor a la variable de la clase.
            this.pacienteId = it.pacienteId

            binding.tvNombreCompletoPaciente.text = it.nombreCompleto
            binding.tvCedulaPaciente.text = "Cédula: ${it.cedulaPaciente}"
            val edad = Utils.calcularEdadDetallada(it.fechaNacimientoPaciente)
            binding.tvEdad.text = "Edad: ${edad.anios} años, ${edad.meses} meses y ${edad.dias} días"
            // Fecha y hora programada
            binding.tvFechaProgramada.text = if (it.fechaHoraProgramadaConsulta != null) {
                "Fecha programada: ${it.fechaHoraProgramadaConsulta.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
            } else {
                "Fecha programada: No disponible"
            }
            
            binding.tvHoraProgramda.text = if (it.fechaHoraProgramadaConsulta != null) {
                "Hora programada: ${it.fechaHoraProgramadaConsulta.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))}"
            } else {
                "Hora programada: No disponible"
            }
            
            // Fecha y hora realizada
            binding.tvFechaRealizada.text = if (it.fechaHoraRealConsulta != null) {
                "Fecha realizada: ${it.fechaHoraRealConsulta.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
            } else {
                "Fecha realizada: No disponible"
            }
            
            binding.tvHoraRealizada.text = if (it.fechaHoraRealConsulta != null) {
                "Hora realizada: ${it.fechaHoraRealConsulta.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))}"
            } else {
                "Hora realizada: No disponible"
            }
            binding.tvEstado.text = it.estadoConsulta.displayValue

            val colorResId = when (it.estadoConsulta) {
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
        binding.cardViewDetallesConsulta.setOnClickListener {
            pacienteId?.let { id ->
                val timestamp = System.currentTimeMillis()
                android.util.Log.d("NavFlow", "AccionesConsultaFragment: Navegando a RegistrarConsultaGraph (detalles) con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=${args.idConsulta}")
                
                val action = AccionesConsultaFragmentDirections.actionAccionesConsultaFragmentToRegistrarConsultaGraph(
                    idPaciente = id,
                    idConsulta = args.idConsulta,
                    isEditable = false,
                    isHistoria = isHistoria
                )
                findNavController().navigate(action)
            } ?: run {
                Utils.mostrarSnackbar(requireView(), "Cargando datos, por favor espere...")
            }
        }

        binding.cardViewModificarConsulta.setOnClickListener {
            pacienteId?.let { id ->
                val timestamp = System.currentTimeMillis()
                android.util.Log.d("NavFlow", "AccionesConsultaFragment: Navegando a RegistrarConsultaGraph (modificar) con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=${args.idConsulta}")
                
                val action = AccionesConsultaFragmentDirections.actionAccionesConsultaFragmentToRegistrarConsultaGraph(
                    idPaciente = id,
                    idConsulta = args.idConsulta,
                    isEditable = true,
                    isHistoria = isHistoria
                )
                findNavController().navigate(action)
            } ?: run {
                Utils.mostrarSnackbar(requireView(), "Cargando datos, por favor espere...")
            }
        }
        binding.cardViewDetallesPaciente.setOnClickListener {
            pacienteId?.let { id ->
                val timestamp = System.currentTimeMillis()
                android.util.Log.d("NavFlow", "AccionesConsultaFragment: Navegando a RegistrarConsultaGraph (modificar) con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=${args.idConsulta}")

                val action = AccionesConsultaFragmentDirections.actionAccionesConsultaFragmentToAccionesPacienteFragment(
                    idPaciente = id,
                )
                findNavController().navigate(action)
            } ?: run {
                Utils.mostrarSnackbar(requireView(), "Cargando datos, por favor espere...")
            }
        }

        binding.cardViewBorrarConsulta.setOnClickListener {
            Utils.mostrarDialog(
                requireContext(),
                "Borrar consulta",
                "¿Está seguro que desea borrar la consulta?",
                "Borrar",
                "No",
                { },
                { },
                true
            )
        }
    }
}