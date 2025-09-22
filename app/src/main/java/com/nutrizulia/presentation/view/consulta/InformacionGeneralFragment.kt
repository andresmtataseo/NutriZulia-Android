package com.nutrizulia.presentation.view.consulta

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.textfield.TextInputLayout
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentRegistrarConsultaBinding
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.presentation.viewmodel.consulta.ConsultaSharedViewModel
import com.nutrizulia.presentation.viewmodel.consulta.InformacionGeneralViewModel
import com.nutrizulia.util.ModoConsulta
import com.nutrizulia.util.Utils
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class InformacionGeneralFragment : Fragment() {

    private val viewModel: InformacionGeneralViewModel by viewModels()
    private val sharedViewModel: ConsultaSharedViewModel by navGraphViewModels(R.id.registrarConsultaGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var binding: FragmentRegistrarConsultaBinding
    private val args: InformacionGeneralFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timestamp = System.currentTimeMillis()
        
        // Obtener isHistoria del SharedViewModel o de los argumentos como fallback
        val isHistoria = sharedViewModel.isHistoria.value ?: args.isHistoria
        
        // Asegurar que el valor esté actualizado en el SharedViewModel
        if (sharedViewModel.isHistoria.value != isHistoria) {
            sharedViewModel.setIsHistoria(isHistoria)
        }
        
        android.util.Log.d("NavFlow", "InformacionGeneralFragment: onViewCreated con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=${args.idConsulta}")
        setupListeners()
        setupObservers()
        sharedViewModel.initialize(args.idPaciente, args.idConsulta, args.isEditable)
    }

    private fun setupListeners() {
        binding.dropdownTipoActividad.bind(
            viewLifecycleOwner,
            viewModel.tiposActividades,
            toText = { it.nombre },
            onItemSelected = { viewModel.selectTipoActividad(it) },
            filter = { it.id in listOf(1, 7, 10) }
        )
        binding.dropdownTipoActividad.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && !binding.tfTipoActividad.editText?.text.isNullOrBlank()) {
                (view as? AutoCompleteTextView)?.adapter?.let { (it as Filterable).filter.filter(null) }
            }
        }

        binding.dropdownEspecialidades.bind(
            viewLifecycleOwner,
            viewModel.especialidades,
            toText = { it.nombre },
            onItemSelected = { viewModel.selectEspecialidad(it) }
        )
        binding.dropdownEspecialidades.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && !binding.tfEspecialidad.editText?.text.isNullOrBlank()) {
                (view as? AutoCompleteTextView)?.adapter?.let { (it as Filterable).filter.filter(null) }
            }
        }

//        binding.dropdownTipoConsulta.bind(
//            viewLifecycleOwner,
//            viewModel.tiposConsultas,
//            toText = { it.displayValue },
//            onItemSelected = { viewModel.selectTipoConsulta(it) }
//        )
//        binding.dropdownTipoConsulta.setOnFocusChangeListener { view, hasFocus ->
//            if (hasFocus && !binding.tfTipoConsulta.editText?.text.isNullOrBlank()) {
//                (view as? AutoCompleteTextView)?.adapter?.let { (it as Filterable).filter.filter(null) }
//            }
//        }

        binding.btnSiguiente.setOnClickListener {
            if (viewModel.areFieldsEditable(sharedViewModel.consulta.value) && !viewModel.validateAndPrepareData()) {
                return@setOnClickListener
            }
            val tipoActividad = viewModel.selectedTipoActividad.value
            val especialidad = viewModel.selectedEspecialidad.value
            val tipoConsulta = viewModel.selectedTipoConsulta.value

            if (tipoActividad != null && especialidad != null && tipoConsulta != null) {
                sharedViewModel.updateConsultaParcial(
                    tipoActividad,
                    especialidad,
                    tipoConsulta,
                    binding.tfMotivoConsulta.editText?.text?.toString()
                )
                val timestamp = System.currentTimeMillis()
                val isHistoria = args.isHistoria
                android.util.Log.d("NavFlow", "InformacionGeneralFragment: Navegando a DatosClinicosFragment con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=${args.idConsulta}")
                val action = InformacionGeneralFragmentDirections.actionRegistrarConsultaFragmentToRegistrarConsulta2Fragment()
                // Pasar el argumento isHistoria al siguiente fragmento
                findNavController().currentBackStackEntry?.arguments?.putBoolean("isHistoria", isHistoria)
                findNavController().navigate(action)
            } else {
                mostrarSnackbar(binding.root, "Por favor, complete toda la información requerida.")
            }
        }

        binding.btnLimpiar.setOnClickListener {
            handleClearOrRestoreClick()
        }
    }

    private fun handleClearOrRestoreClick() {
        val modo = sharedViewModel.modoConsulta.value
        val consulta = sharedViewModel.consulta.value

        if (!viewModel.areFieldsEditable(consulta)) return

        if (modo == ModoConsulta.CREAR_SIN_CITA) {
            mostrarDialog(
                requireContext(),
                "Limpiar Formulario",
                "¿Está seguro de que desea limpiar todos los campos?",
                "Sí, Limpiar",
                "Cancelar",
                { clearForm() }
            )
        } else if (consulta != null) {
            mostrarDialog(
                requireContext(),
                "Restaurar Datos",
                "¿Desea restaurar los datos originales de la consulta?",
                "Sí, Restaurar",
                "Cancelar",
                { restoreForm() }
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        sharedViewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let { mostrarSnackbar(binding.root, it) }
        }

        sharedViewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir == true) findNavController().popBackStack()
        }

        sharedViewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            paciente?.let {
                binding.tfNombreCompletoPaciente.editText?.setText("${it.nombres} ${it.apellidos}")
                binding.tfGeneroPaciente.editText?.setText(it.genero)
                val edad = Utils.calcularEdadDetallada(it.fechaNacimiento)
                binding.tfEdadPaciente.editText?.setText("${edad.anios} años, ${edad.meses} meses y ${edad.dias} días")
            }
        }

        sharedViewModel.consultaEditando.observe(viewLifecycleOwner) { consultaEnEdicion ->
            if (consultaEnEdicion == null) return@observe

            val sonEditables = viewModel.areFieldsEditable(consultaEnEdicion)
            viewModel.loadInitialData(
                pacienteId = sharedViewModel.paciente.value?.id ?: args.idPaciente,
                isEditable = sonEditables,
                consultaPrevia = consultaEnEdicion,
                consultaOriginal = sharedViewModel.consulta.value
            )

            updateCleanButtonState(sharedViewModel.modoConsulta.value, consultaEnEdicion)
            binding.tfMotivoConsulta.editText?.setText(consultaEnEdicion.motivoConsulta.orEmpty())

            binding.contentProgramacion.visibility = if (consultaEnEdicion.fechaHoraProgramada != null) View.VISIBLE else View.GONE
            if (consultaEnEdicion.fechaHoraProgramada != null) {
                binding.tfFechaCita.editText?.setText(consultaEnEdicion.fechaHoraProgramada.format(DateTimeFormatter.ISO_LOCAL_DATE))
                binding.tfHoraCita.editText?.setText(consultaEnEdicion.fechaHoraProgramada.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US)))
            }
        }

        sharedViewModel.modoConsulta.observe(viewLifecycleOwner) { modo ->
            if (modo == null) return@observe
            updateCleanButtonState(modo, sharedViewModel.consulta.value)

            when (modo) {
                ModoConsulta.VER_CONSULTA, ModoConsulta.CULMINAR_CITA -> deshabilitarCampos()
                ModoConsulta.CREAR_SIN_CITA -> {
                    viewModel.loadInitialData(
                        pacienteId = sharedViewModel.paciente.value?.id ?: args.idPaciente,
                        isEditable = true,
                        consultaPrevia = null,
                        consultaOriginal = null)

                    habilitarCampos()
                }
                else -> habilitarCampos()
            }
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            clearErrors()
            errores.forEach { (key, message) ->
                when (key) {
                    "tipoActividad" -> Utils.mostrarErrorEnCampo(binding.tfTipoActividad, message)
                    "especialidad" -> Utils.mostrarErrorEnCampo(binding.tfEspecialidad, message)
                    "tipoConsulta" -> Utils.mostrarErrorEnCampo(binding.tfTipoConsulta, message)
                }
            }
        }

        viewModel.selectedTipoActividad.observe(viewLifecycleOwner) { tipoActividad ->
            (binding.tfTipoActividad.editText as? AutoCompleteTextView)?.setText(tipoActividad?.nombre, false)
        }

        viewModel.selectedEspecialidad.observe(viewLifecycleOwner) { especialidad ->
            (binding.tfEspecialidad.editText as? AutoCompleteTextView)?.setText(especialidad?.nombre, false)
        }

        viewModel.selectedTipoConsulta.observe(viewLifecycleOwner) { tipoConsulta ->
            (binding.tfTipoConsulta.editText as? AutoCompleteTextView)?.setText(tipoConsulta?.displayValue, false)
        }
    }

    private fun clearForm() {
        clearErrors()
        binding.tfTipoActividad.editText?.text?.clear()
        binding.tfEspecialidad.editText?.text?.clear()
        binding.tfMotivoConsulta.editText?.text?.clear()
        viewModel.clearSelections()
    }

    private fun restoreForm() {
        clearErrors()
        viewModel.restoreOriginalData()
        val originalMotive = sharedViewModel.consulta.value?.motivoConsulta.orEmpty()
        binding.tfMotivoConsulta.editText?.setText(originalMotive)
    }

    private fun clearErrors() {
        binding.tfTipoActividad.error = null
        binding.tfEspecialidad.error = null
    }

    private fun habilitarCampos() {
        val sonEditables = viewModel.areFieldsEditable(sharedViewModel.consulta.value)
        if (sonEditables) {
            binding.tiMotivoConsulta.isEnabled = true
            listOf(
                binding.dropdownTipoActividad,
                binding.dropdownEspecialidades,
            ).forEach { it.isEnabled = true }
        }

        binding.btnSiguiente.visibility = View.VISIBLE
    }

    private fun deshabilitarCampos() {
        binding.tiMotivoConsulta.isEnabled = false
        listOf(
            binding.dropdownTipoActividad,
            binding.dropdownEspecialidades
        ).forEach {
            it.isEnabled = false
            (it.parent.parent as? TextInputLayout)?.endIconMode = TextInputLayout.END_ICON_NONE
        }
        binding.btnSiguiente.visibility = View.VISIBLE
    }

    private fun updateCleanButtonState(modo: ModoConsulta?, consulta: Consulta?) {
        if (modo == ModoConsulta.VER_CONSULTA) {
            binding.btnLimpiar.visibility = View.GONE
            return
        }

        val areFieldsEditable = viewModel.areFieldsEditable(consulta)
        binding.btnLimpiar.visibility = if (areFieldsEditable) View.VISIBLE else View.GONE
        if (areFieldsEditable) {
            binding.btnLimpiar.text = if (modo == ModoConsulta.CREAR_SIN_CITA) {
                getString(R.string.limpiar)
            } else {
                getString(R.string.restaurar)
            }
        }
    }

    private fun <T> AutoCompleteTextView.bind(
        lifecycleOwner: LifecycleOwner,
        itemsLive: LiveData<List<T>>,
        toText: (T) -> String,
        onItemSelected: (T) -> Unit,
        filter: ((T) -> Boolean)? = null
    ) {
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line)
        setAdapter(adapter)

        var currentItems: List<T> = emptyList()

        val updateAdapter: (List<T>?) -> Unit = { items ->
            // Aplicar filtro si se proporciona
            currentItems = if (filter != null && items != null) {
                items.filter(filter)
            } else {
                items ?: emptyList()
            }
            adapter.clear()
            adapter.addAll(currentItems.map(toText))
            adapter.notifyDataSetChanged()
        }

        itemsLive.observe(lifecycleOwner, updateAdapter)

        if (itemsLive.value != null) {
            updateAdapter(itemsLive.value)
        }

        setOnItemClickListener { _, _, position, _ ->
            if (position < currentItems.size) {
                onItemSelected(currentItems[position])
            }
        }
    }
}