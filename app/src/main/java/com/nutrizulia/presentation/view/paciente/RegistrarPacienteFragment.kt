package com.nutrizulia.presentation.view.paciente

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nutrizulia.databinding.FragmentRegistrarPacienteBinding
import com.nutrizulia.domain.model.catalog.Estado
import com.nutrizulia.domain.model.catalog.Etnia
import com.nutrizulia.domain.model.catalog.Municipio
import com.nutrizulia.domain.model.catalog.Nacionalidad
import com.nutrizulia.domain.model.catalog.Parroquia
import com.nutrizulia.presentation.viewmodel.paciente.RegistrarPacienteViewModel
import com.nutrizulia.util.Utils.calcularEdad
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class RegistrarPacienteFragment : Fragment() {

    private val viewModel: RegistrarPacienteViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarPacienteBinding
    private val args: RegistrarPacienteFragmentArgs by navArgs()
    private var ultimaFechaSeleccionada: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrarPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate(args.pacienteId, args.isEditable)
        setupObservers()
        setupListeners()

        if (!args.isEditable) {
            deshabilitarCampos()
            binding.btnRegistrar.text = "Salir"
            binding.btnRegistrar.icon = ContextCompat.getDrawable(requireContext(), com.nutrizulia.R.drawable.ic_atras)
        } else {
            if (args.pacienteId != null) {
                binding.btnLimpiar.text = "Restaurar"
            }
        }
    }

    private fun setupObservers() {
        observerRepresentante()
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            if (mensaje.isNotBlank()) mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack()
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            quitarErrores()
            errores.forEach { (key, message) ->
                when (key) {
                    "esCedulado" -> mostrarErrorEnCampo(binding.tfEsCedulado, message)
                    "tipoCedula" -> mostrarErrorEnCampo(binding.tfTipoCedula, message)
                    "cedula" -> mostrarErrorEnCampo(binding.tfCedula, message)
                    "nombres" -> mostrarErrorEnCampo(binding.tfNombres, message)
                    "apellidos" -> mostrarErrorEnCampo(binding.tfApellidos, message)
                    "fechaNacimiento" -> mostrarErrorEnCampo(binding.tfFechaNacimiento, message)
                    "genero" -> mostrarErrorEnCampo(binding.tfGenero, message)
                    "etnia" -> mostrarErrorEnCampo(binding.tfEtnia, message)
                    "nacionalidad" -> mostrarErrorEnCampo(binding.tfNacionalidad, message)
                    "estado" -> mostrarErrorEnCampo(binding.tfEstado, message)
                    "municipio" -> mostrarErrorEnCampo(binding.tfMunicipio, message)
                    "parroquia" -> mostrarErrorEnCampo(binding.tfParroquia, message)
                    "telefono" -> mostrarErrorEnCampo(binding.tfTelefono, message)
                    "correo" -> mostrarErrorEnCampo(binding.tfEmail, message)
                    "representante", "parentesco", "cedulaTemporal" -> {
                        // Para errores relacionados con representante, mostrar snackbar
                        mostrarSnackbar(binding.root, message)
                    }
                }
            }
        }
        
        // Observer para el estado de validación de cédula en tiempo real
        viewModel.cedulaValidationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                RegistrarPacienteViewModel.CedulaValidationState.IDLE -> {
                    binding.tfCedula.error = null
                    binding.tfCedula.helperText = null
                }
                RegistrarPacienteViewModel.CedulaValidationState.VALIDATING -> {
                    binding.tfCedula.error = null
                    binding.tfCedula.helperText = "Validando..."
                }
                RegistrarPacienteViewModel.CedulaValidationState.VALID -> {
                    binding.tfCedula.error = null
                    binding.tfCedula.helperText = "Cédula disponible"
                }
                RegistrarPacienteViewModel.CedulaValidationState.DUPLICATE -> {
                    binding.tfCedula.error = "Esta cédula ya está registrada"
                    binding.tfCedula.helperText = null
                }
                RegistrarPacienteViewModel.CedulaValidationState.INVALID -> {
                    binding.tfCedula.error = "Formato de cédula inválido"
                    binding.tfCedula.helperText = null
                }
            }
        }

        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            binding.tfNombres.editText?.setText(paciente.nombres)
            binding.tfApellidos.editText?.setText(paciente.apellidos)
            binding.tfFechaNacimiento.editText?.setText(paciente.fechaNacimiento.toString())
            (binding.tfGenero.editText as? AutoCompleteTextView)?.setText(paciente.genero, false)
            val telefonoParts = paciente.telefono?.split("-")
            (binding.tfPrefijo.editText as? AutoCompleteTextView)?.setText(telefonoParts?.getOrNull(0) ?: "", false)
            binding.tfTelefono.editText?.setText(telefonoParts?.getOrNull(1) ?: "")
            binding.tfEmail.editText?.setText(paciente.correo)
            binding.tfDomicilio.editText?.setText(paciente.domicilio)
        }
        
        viewModel.esCedulado.observe(viewLifecycleOwner) { esCedulado ->
            if (esCedulado) {
                // Paciente cedulado
                binding.dropdownEsCedulado.setText("Si", false)
                binding.layoutCedulado.visibility = View.VISIBLE
                binding.tfCedulaTemporal.visibility = View.GONE
                binding.layoutNoCeduladoMenorEdadRepresentante.visibility = View.GONE
                
                // Limpiar representante cuando se cambia a cedulado
                viewModel.limpiarRepresentante()
                
                // Mostrar la cédula en los campos correspondientes
                viewModel.paciente.value?.let { paciente ->
                    val cedulaParts = paciente.cedula.split("-")
                    (binding.tfTipoCedula.editText as? AutoCompleteTextView)?.setText(cedulaParts.getOrNull(0) ?: "", false)
                    binding.tfCedula.editText?.setText(cedulaParts.getOrNull(1) ?: "")
                }
            } else {
                // Paciente no cedulado (con cédula temporal)
                binding.dropdownEsCedulado.setText("No", false)
                binding.layoutCedulado.visibility = View.GONE
                binding.tfCedulaTemporal.visibility = View.VISIBLE
                binding.layoutNoCeduladoMenorEdadRepresentante.visibility = View.VISIBLE
                
                // Limpiar campos de cédula
                (binding.tfTipoCedula.editText as? AutoCompleteTextView)?.setText("", false)
                binding.tfCedula.editText?.setText("")
            }
        }

        viewModel.etnias.observe(viewLifecycleOwner) { updateAdapter(binding.dropdownEtnias, it.map(Etnia::nombre)) }
        viewModel.nacionalidades.observe(viewLifecycleOwner) { updateAdapter(binding.dropdownNacionalidades, it.map(Nacionalidad::nombre)) }
        viewModel.estados.observe(viewLifecycleOwner) { updateAdapter(binding.dropdownEstados, it.map(Estado::nombre)) }
        viewModel.municipios.observe(viewLifecycleOwner) { updateAdapter(binding.dropdownMunicipios, it.map(Municipio::nombre)) }
        viewModel.parroquias.observe(viewLifecycleOwner) { updateAdapter(binding.dropdownParroquias, it.map(Parroquia::nombre)) }

        viewModel.selectedEtnia.observe(viewLifecycleOwner) { binding.dropdownEtnias.setText(it?.nombre, false) }
        viewModel.selectedNacionalidad.observe(viewLifecycleOwner) { binding.dropdownNacionalidades.setText(it?.nombre, false) }
        viewModel.selectedParroquia.observe(viewLifecycleOwner) { binding.dropdownParroquias.setText(it?.nombre, false) }

        viewModel.selectedEstado.observe(viewLifecycleOwner) { estado ->
            binding.dropdownEstados.setText(estado?.nombre, false)
            binding.tfMunicipio.visibility = if (estado != null) View.VISIBLE else View.GONE
        }
        viewModel.selectedMunicipio.observe(viewLifecycleOwner) { municipio ->
            binding.dropdownMunicipios.setText(municipio?.nombre, false)
            binding.tfParroquia.visibility = if (municipio != null) View.VISIBLE else View.GONE
        }
    }

    private fun setupListeners() {
        configurarDropdownCedulado()
        listenerRepresentante()
        mostrarSelectorFecha(binding.tfFechaNacimiento.editText as TextInputEditText)
        configurarLimpiezaErrores()

        (binding.tfTipoCedula.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            val adapter = (binding.tfTipoCedula.editText as AutoCompleteTextView).adapter
            val selectedType = adapter.getItem(position) as String
            viewModel.onTipoCedulaSelected(selectedType)
            // Limpiar error al seleccionar
            binding.tfTipoCedula.error = null
            
            // Revalidar la cédula actual si hay texto
            val cedulaActual = obtenerTexto(binding.tfCedula)
            if (cedulaActual.isNotBlank()) {
                viewModel.validateCedulaRealTime(selectedType, cedulaActual)
            }
        }

        binding.btnSeleccinarRepresentate.setOnClickListener {
            showRepresentativeDialog()
        }
        
        binding.btnRemoverRepresentante.setOnClickListener {
            viewModel.removerRepresentante()
        }

        binding.btnRegistrar.setOnClickListener {
            if (args.isEditable) {
                val textoEsCedulado = binding.dropdownEsCedulado.text.toString()
                val esCedulado = when (textoEsCedulado) {
                    "Si" -> true
                    "No" -> false
                    else -> null // No se ha seleccionado nada
                }
                viewModel.onSavePatientClicked(
                    id = args.pacienteId,
                    esCedulado = esCedulado,
                    tipoCedula = obtenerTexto(binding.tfTipoCedula),
                    cedula = obtenerTexto(binding.tfCedula),
                    nombres = obtenerTexto(binding.tfNombres),
                    apellidos = obtenerTexto(binding.tfApellidos),
                    fechaNacimientoStr = obtenerTexto(binding.tfFechaNacimiento),
                    genero = obtenerTexto(binding.tfGenero),
                    domicilio = obtenerTexto(binding.tfDomicilio),
                    prefijo = obtenerTexto(binding.tfPrefijo),
                    telefono = obtenerTexto(binding.tfTelefono),
                    correo = obtenerTexto(binding.tfEmail)
                )
            } else {
                findNavController().popBackStack()
            }
        }

        binding.btnLimpiar.setOnClickListener {
            val dialogTitle = if (args.pacienteId == null) "Limpiar campos" else "Restaurar cambios"
            val dialogMessage = if (args.pacienteId == null) "¿Desea limpiar todos los campos?" else "¿Desea restaurar los cambios?"
            val positiveButton = if (args.pacienteId == null) "Limpiar" else "Restaurar"
            mostrarDialog(requireContext(), dialogTitle, dialogMessage, positiveButton, "No", {
                limpiarCampos()
                if (args.pacienteId != null) viewModel.onCreate(args.pacienteId, true)
            }, {}, true)
        }

        binding.dropdownEtnias.setOnItemClickListener { _, _, position, _ ->
            viewModel.etnias.value?.get(position)?.let { viewModel.onEtniaSelected(it) }
            binding.tfEtnia.error = null
        }
        binding.dropdownNacionalidades.setOnItemClickListener { _, _, position, _ ->
            viewModel.nacionalidades.value?.get(position)?.let { viewModel.onNacionalidadSelected(it) }
            binding.tfNacionalidad.error = null
        }
        binding.dropdownEstados.setOnItemClickListener { _, _, position, _ ->
            viewModel.estados.value?.get(position)?.let { viewModel.onEstadoSelected(it) }
            binding.tfEstado.error = null
        }
        binding.dropdownMunicipios.setOnItemClickListener { _, _, position, _ ->
            viewModel.municipios.value?.get(position)?.let { viewModel.onMunicipioSelected(it) }
            binding.tfMunicipio.error = null
        }
        binding.dropdownParroquias.setOnItemClickListener { _, _, position, _ ->
            viewModel.parroquias.value?.get(position)?.let { viewModel.onParroquiaSelected(it) }
            binding.tfParroquia.error = null
        }
    }

    private fun configurarLimpiezaErrores() {
        // Limpiar errores cuando el usuario empieza a escribir
        binding.tfCedula.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfCedula.error = null
        }
        binding.tfCedulaTemporal.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfCedulaTemporal.error = null
        }
        
        // Configurar validación en tiempo real para el campo de cédula
        binding.tfCedula.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val cedula = s?.toString() ?: ""
                val tipoCedula = obtenerTexto(binding.tfTipoCedula)
                if (tipoCedula.isNotBlank()) {
                    viewModel.validateCedulaRealTime(tipoCedula, cedula)
                }
            }
        })
        binding.tfNombres.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfNombres.error = null
        }
        binding.tfApellidos.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfApellidos.error = null
        }
        binding.tfFechaNacimiento.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfFechaNacimiento.error = null
        }
        binding.tfTelefono.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfTelefono.error = null
        }
        binding.tfEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfEmail.error = null
        }
        
        // Limpiar errores en dropdowns cuando se abren
        binding.dropdownEsCedulado.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfEsCedulado.error = null
        }
        (binding.tfGenero.editText as? AutoCompleteTextView)?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfGenero.error = null
        }
    }

    private fun deshabilitarCampos() {
        listOf(
            binding.dropdownCedulado,
            binding.dropdownEsCedulado,
            binding.dropdownGenero,
            binding.dropdownEtnias,
            binding.dropdownNacionalidades,
            binding.dropdownEstados,
            binding.dropdownMunicipios,
            binding.dropdownParroquias,
            binding.dropdownPrefijos
        ).forEach {
            it.isEnabled = false
            (it.parent.parent as? TextInputLayout)?.endIconMode = TextInputLayout.END_ICON_NONE
        }

        listOf(
            binding.tiFechaNacimiento,
            binding.tiCedula,
            binding.tiCedulaTemporal,
            binding.tiNombres,
            binding.tiApellidos,
            binding.tiDomicilio,
            binding.tiTelefono,
            binding.tiEmail
        ).forEach {
            it.isEnabled = false
        }

        binding.btnLimpiar.visibility = View.GONE
    }

    private fun updateAdapter(dropdown: AutoCompleteTextView, items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, items)
        dropdown.setAdapter(adapter)
    }

    private fun limpiarCampos() {
        quitarErrores()
        limpiarRepresentante()
        binding.dropdownEsCedulado.setText("", false)

        binding.layoutCedulado.visibility = View.GONE
        binding.tfCedulaTemporal.visibility = View.GONE
        binding.layoutNoCeduladoMenorEdadRepresentante.visibility = View.GONE

        binding.tfTipoCedula.editText?.text = null
        binding.tfCedulaTemporal.editText?.text = null
        binding.tfCedula.editText?.text = null
        binding.tfNombres.editText?.text = null
        binding.tfApellidos.editText?.text = null
        binding.tfFechaNacimiento.editText?.text = null
        binding.tfGenero.editText?.text = null
        binding.tfPrefijo.editText?.text = null
        binding.tfTelefono.editText?.text = null
        binding.tfEmail.editText?.text = null
        binding.tfDomicilio.editText?.text = null
        binding.dropdownEtnias.setText("", false)
        binding.dropdownNacionalidades.setText("", false)
        binding.dropdownEstados.setText("", false)
        binding.dropdownMunicipios.setText("", false)
        binding.dropdownParroquias.setText("", false)
    }

    private fun quitarErrores() {
        binding.tfEsCedulado.error = null
        binding.tfTipoCedula.error = null
        binding.tfCedula.error = null
        binding.tfCedulaTemporal.error = null
        binding.tfNombres.error = null
        binding.tfApellidos.error = null
        binding.tfFechaNacimiento.error = null
        binding.tfGenero.error = null
        binding.tfEtnia.error = null
        binding.tfNacionalidad.error = null
        binding.tfEstado.error = null
        binding.tfMunicipio.error = null
        binding.tfParroquia.error = null
        binding.tfPrefijo.error = null
        binding.tfTelefono.error = null
        binding.tfEmail.error = null
    }

    private fun mostrarSelectorFecha(editText: TextInputEditText) {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        fun abrirPicker() {
            val fragmentManager = parentFragmentManager
            val existingPicker = fragmentManager.findFragmentByTag("MaterialDatePicker")
            if (existingPicker != null) {
                fragmentManager.beginTransaction().remove(existingPicker).commit()
            }

            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build()

            val seleccionInicial =
                ultimaFechaSeleccionada ?: MaterialDatePicker.todayInUtcMilliseconds()

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona la fecha")
                .setSelection(seleccionInicial)
                .setCalendarConstraints(constraints)
                .build()

            datePicker.addOnPositiveButtonClickListener { utcDateMillis ->
                ultimaFechaSeleccionada = utcDateMillis

                val localDate = Instant.ofEpochMilli(utcDateMillis)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()

                editText.setText(localDate.format(dateFormatter))
            }

            datePicker.show(fragmentManager, "MaterialDatePicker")
        }

        editText.setOnClickListener { abrirPicker() }
        binding.tfFechaNacimiento.setStartIconOnClickListener { abrirPicker() }
    }

    private fun configurarDropdownCedulado() {
        val dropdown = binding.dropdownEsCedulado
        dropdown.setOnItemClickListener { _, _, position, _ ->
            // Limpiar errores relacionados con cédula y representante
            binding.tfTipoCedula.error = null
            binding.tfCedula.error = null
            
            when (position) {
                0 -> { // "Si"
                    binding.layoutCedulado.visibility = View.VISIBLE
                    binding.layoutNoCeduladoMenorEdadRepresentante.visibility = View.GONE
                    binding.tfCedulaTemporal.visibility = View.GONE
                    limpiarRepresentante()
                }
                1 -> { // "No"
                    binding.layoutNoCeduladoMenorEdadRepresentante.visibility = View.VISIBLE
                    binding.layoutCedulado.visibility = View.GONE
                    binding.tfCedulaTemporal.visibility = View.VISIBLE
                    binding.tfCedula.editText?.text = null
                    binding.tfTipoCedula.editText?.text = null
                }
                else -> { // vacio
                    binding.layoutNoCeduladoMenorEdadRepresentante.visibility = View.GONE
                    binding.layoutCedulado.visibility = View.GONE
                    binding.tfCedulaTemporal.visibility = View.GONE
                    limpiarRepresentante()
                    binding.tfCedula.editText?.text = null
                    binding.tfTipoCedula.editText?.text = null
                }
            }
        }
    }

     // Dialog representantes

    private fun showRepresentativeDialog() {
        val representativeDialog = SeleccionarRepresentanteFragment()
        representativeDialog.show(parentFragmentManager, "RepresentativeSelectionDialog")
    }

    private fun listenerRepresentante() {
        parentFragmentManager.setFragmentResultListener("representante_seleccionado", viewLifecycleOwner) { _, bundle ->
            val representanteId = bundle.getString("representanteId")
            val parentescoId = bundle.getString("parentescoId")?.toInt()

            if (representanteId != null && parentescoId != null) {
                viewModel.onRepresentanteSelected(representanteId, parentescoId)

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observerRepresentante() {
        viewModel.representante.observe(viewLifecycleOwner) { representante ->
            if (representante != null) {
                binding.cardPaciente.visibility = View.VISIBLE
                binding.tvNombreCompleto.setText("${representante.nombres} ${representante.apellidos}")
                binding.tvCedula.setText("Cédula: ${representante.cedula}")
                binding.tvGenero.setText("Género: ${representante.genero}")
                binding.tvFechaNacimiento.setText("Fecha de nacimiento: ${representante.fechaNacimiento}")
                binding.tvEdad.setText("Edad: ${calcularEdad(representante.fechaNacimiento)} años")
                binding.tvSinRepresentante.visibility = View.GONE
                
                // Mostrar/ocultar botones según el estado del representante
                binding.btnSeleccinarRepresentate.visibility = View.GONE
                binding.btnRemoverRepresentante.visibility = View.VISIBLE
            } else {
                binding.cardPaciente.visibility = View.GONE
                binding.tvSinRepresentante.visibility = View.VISIBLE
                binding.btnSeleccinarRepresentate.visibility = View.VISIBLE
                binding.btnRemoverRepresentante.visibility = View.GONE
                binding.btnSeleccinarRepresentate.text = "Seleccionar representante"
                binding.btnSeleccinarRepresentate.isEnabled = true
            }
        }
        viewModel.cedulaTemporal.observe(viewLifecycleOwner) { cedulaTemporal ->
            binding.tfCedulaTemporal.editText?.setText(cedulaTemporal)
        }
        viewModel.selectedParentesco.observe(viewLifecycleOwner) { parentesco ->
            binding.tvParentesco.setText("Parentesco: ${parentesco?.nombre}")
        }
    }

    private fun limpiarRepresentante() {
        binding.cardPaciente.visibility = View.GONE
        binding.tvSinRepresentante.visibility = View.VISIBLE
        viewModel.limpiarRepresentante()
    }

}