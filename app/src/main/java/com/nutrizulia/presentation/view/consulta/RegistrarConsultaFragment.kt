package com.nutrizulia.presentation.view.consulta

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.databinding.FragmentRegistrarConsultaBinding
import com.nutrizulia.domain.model.catalog.Especialidad
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.presentation.viewmodel.RegistrarConsultaViewModel
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class RegistrarConsultaFragment : Fragment() {

    private val viewModel: RegistrarConsultaViewModel by navGraphViewModels(R.id.registrarConsultaGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var binding: FragmentRegistrarConsultaBinding
    private val args: RegistrarConsultaFragmentArgs by navArgs()
    private var tipoActividadSel: TipoActividad? = null
    private var especialidadSel: Especialidad? = null
    private var tipoConsultaSel: TipoConsulta? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        if (savedInstanceState == null) {
            viewModel.onCreate(
                args.idPaciente,
                args.idConsulta,
                args.isEditable
            )
        }

        if (args.idConsulta != null && !args.isEditable) {
            deshabilitarCampos()
        }

        setupListeners()
    }


    private fun setupListeners() {

        binding.dropdownTipoActividad.bind(
            viewLifecycleOwner, viewModel.tiposActividades,
            toText = { it.nombre },
            onItemSelected = { et ->
                tipoActividadSel = et
            }
        )

        binding.dropdownEspecialidades.bind(
            viewLifecycleOwner, viewModel.especialidades,
            toText = { it.nombre },
            onItemSelected = { na ->
                especialidadSel = na
            }
        )

        binding.dropdownTipoConsulta.bind(
            viewLifecycleOwner, viewModel.tiposConsultas,
            toText = { it.displayValue },
            onItemSelected = { na ->
                tipoConsultaSel = na
            }
        )

        binding.btnSiguiente.setOnClickListener {
            val esValido = viewModel.validarConsulta(tipoActividadSel, especialidadSel, tipoConsultaSel)
            if (!esValido) return@setOnClickListener
            // Siempre guardar la consulta parcial para que consultaEditando esté inicializada
            viewModel.guardarConsultaParcial(
                tipoActividadSel!!,
                especialidadSel!!,
                tipoConsultaSel!!,
                binding.tfMotivoConsulta.editText?.text?.toString()
            )
            findNavController().navigate(
                RegistrarConsultaFragmentDirections.actionRegistrarConsultaFragmentToRegistrarConsulta2Fragment(
                    idPaciente = args.idPaciente,
                    idConsulta = viewModel.consultaEditando.value?.id ?: args.idConsulta ?: Utils.generarUUID(),
                    isEditable = args.isEditable
                )
            )
        }

        binding.btnLimpiar.setOnClickListener {
            Utils.mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Desea limpiar todos los campos?",
                "Sí",
                "No",
                { limpiarCampos() },
                { },
                true
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            Utils.mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack(R.id.consultasFragment, false)
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            quitarErrores()
            errores.forEach { (key, message) ->
                when (key) {
                    "tipoActividad" -> Utils.mostrarErrorEnCampo(binding.tfTipoActividad, message)
                    "tipoConsulta" -> Utils.mostrarErrorEnCampo(binding.tfTipoConsulta, message)
                    "especialidad" -> Utils.mostrarErrorEnCampo(binding.tfEspecialidad, message)
                }
            }
        }

        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            binding.tfNombreCompletoPaciente.editText?.setText("${paciente.nombres} ${paciente.apellidos}")
            binding.tfGeneroPaciente.editText?.setText(paciente.genero)
            val edad = Utils.calcularEdadDetallada(paciente.fechaNacimiento)
            binding.tfEdadPaciente.editText?.setText("${edad.anios} años, ${edad.meses} meses y ${edad.dias} días")
        }

        viewModel.consulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta != null) {
                binding.tfMotivoConsulta.editText?.setText(consulta.motivoConsulta.orEmpty())
                binding.tfFechaCita.editText?.setText(
                    consulta.fechaHoraProgramada?.format(
                        DateTimeFormatter.ISO_LOCAL_DATE
                    ).orEmpty()
                )
                binding.tfHoraCita.editText?.setText(
                    consulta.fechaHoraProgramada?.format(
                        DateTimeFormatter.ofPattern("h:mm a", Locale.US)
                    ).orEmpty()
                )

                binding.tfMotivoConsulta.visibility =
                    if (!consulta.motivoConsulta.isNullOrBlank()) View.VISIBLE else View.GONE

                binding.contentProgramacion.visibility =
                    if (consulta.fechaHoraProgramada != null) View.VISIBLE else View.GONE
            }
        }

        viewModel.tipoActividad.observe(viewLifecycleOwner) { it ->
            val tipoActividadDropdown = binding.tfTipoActividad.editText as? AutoCompleteTextView
            tipoActividadDropdown?.setText(it.nombre, false)
            tipoActividadSel = it
        }

        viewModel.especialidad.observe(viewLifecycleOwner) { it ->
            val especialidadDropdown = binding.tfEspecialidad.editText as? AutoCompleteTextView
            especialidadDropdown?.setText(it.nombre, false)
            especialidadSel = it
        }

        viewModel.tipoConsulta.observe(viewLifecycleOwner) { it ->
            val tipoConsultaDropdown = binding.tfTipoConsulta.editText as? AutoCompleteTextView
            tipoConsultaDropdown?.setText(it.displayValue, false)
            tipoConsultaSel = it
        }

    }

    private fun limpiarCampos() {
        quitarErrores()
        binding.tfTipoActividad.editText?.text?.clear()
        binding.tfEspecialidad.editText?.text?.clear()
        binding.tfTipoConsulta.editText?.text?.clear()
        binding.tfMotivoConsulta.editText?.text?.clear()
    }

    private fun quitarErrores() {
        binding.tfTipoActividad.error = null
        binding.tfEspecialidad.error = null
        binding.tfTipoConsulta.error = null
    }

    private fun deshabilitarCampos() {
        val campos = listOf(
            binding.tfTipoActividad,
            binding.tfEspecialidad,
            binding.tfTipoConsulta,
            binding.tfMotivoConsulta,
            binding.tfFechaCita,
            binding.tfHoraCita
        )
        campos.forEach { it.isEnabled = false }

        binding.btnLimpiar.visibility = View.GONE
        binding.btnSiguiente.visibility = View.VISIBLE
    }

    private fun <T> AutoCompleteTextView.bind(
        lifecycleOwner: LifecycleOwner,
        itemsLive: LiveData<List<T>>,
        toText: (T) -> String,
        onItemSelected: (T) -> Unit
    ) {
        var currentItems: List<T> = emptyList()

        itemsLive.observe(lifecycleOwner) { items ->
            currentItems = items
            val names = items.map(toText)
            val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, names)
            setAdapter(adapter)
            if (text.toString() !in names) {
                setText("", false)
            }
        }

        setOnItemClickListener { _, _, position, _ ->
            onItemSelected(currentItems[position])
        }
    }
}