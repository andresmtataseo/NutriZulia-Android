package com.nutrizulia.presentation.view.representante

import android.R
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nutrizulia.databinding.FragmentRegistrarRepresentanteBinding
import com.nutrizulia.domain.model.catalog.Estado
import com.nutrizulia.domain.model.catalog.Etnia
import com.nutrizulia.domain.model.catalog.Municipio
import com.nutrizulia.domain.model.catalog.Nacionalidad
import com.nutrizulia.domain.model.catalog.Parroquia
import com.nutrizulia.presentation.viewmodel.paciente.RegistrarPacienteViewModel
import com.nutrizulia.presentation.viewmodel.representante.RegistrarRepresentanteViewModel
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class RegistrarRepresentanteFragment : Fragment() {

    private var _binding: FragmentRegistrarRepresentanteBinding? = null
    private val binding: FragmentRegistrarRepresentanteBinding get() = _binding!!
    private val viewModel: RegistrarRepresentanteViewModel by viewModels()
    private val args: RegistrarRepresentanteFragmentArgs by navArgs()
    private var ultimaFechaSeleccionada: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrarRepresentanteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate(args.representanteId, args.isEditable)
        setupObservers()
        setupListeners()

        if (!args.isEditable) {
            deshabilitarCampos()
            binding.btnRegistrar.text = "Salir"
            binding.btnRegistrar.icon = ContextCompat.getDrawable(requireContext(), com.nutrizulia.R.drawable.ic_atras)
        } else {
            if (args.representanteId != null) {
                binding.btnLimpiar.text = "Restaurar"
            }
        }
    }

    private fun setupObservers() {
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
                }
            }
        }

        // Observer para el estado de validación de cédula en tiempo real
        viewModel.cedulaValidationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                RegistrarRepresentanteViewModel.CedulaValidationState.IDLE -> {
                    binding.tfCedula.error = null
                    binding.tfCedula.helperText = null
                }
                RegistrarRepresentanteViewModel.CedulaValidationState.VALIDATING -> {
                    binding.tfCedula.error = null
                    binding.tfCedula.helperText = "Validando..."
                }
                RegistrarRepresentanteViewModel.CedulaValidationState.VALID -> {
                    binding.tfCedula.error = null
                    binding.tfCedula.helperText = "Cédula disponible"
                }
                RegistrarRepresentanteViewModel.CedulaValidationState.DUPLICATE -> {
                    binding.tfCedula.error = "Esta cédula ya está registrada"
                    binding.tfCedula.helperText = null
                }
                RegistrarRepresentanteViewModel.CedulaValidationState.INVALID -> {
                    binding.tfCedula.error = "Formato de cédula inválido"
                    binding.tfCedula.helperText = null
                }
            }
        }

        viewModel.representante.observe(viewLifecycleOwner) { representante ->
            val cedulaParts = representante.cedula.split("-")
            (binding.tfTipoCedula.editText as? AutoCompleteTextView)?.setText(cedulaParts.getOrNull(0) ?: "", false)
            binding.tfCedula.editText?.setText(cedulaParts.getOrNull(1) ?: "")
            binding.tfNombres.editText?.setText(representante.nombres)
            binding.tfApellidos.editText?.setText(representante.apellidos)
            binding.tfFechaNacimiento.editText?.setText(representante.fechaNacimiento.toString())
            (binding.tfGenero.editText as? AutoCompleteTextView)?.setText(representante.genero, false)
            val telefonoParts = representante.telefono?.split("-")
            (binding.tfPrefijo.editText as? AutoCompleteTextView)?.setText(telefonoParts?.getOrNull(0) ?: "", false)
            binding.tfTelefono.editText?.setText(telefonoParts?.getOrNull(1) ?: "")
            binding.tfEmail.editText?.setText(representante.correo)
            binding.tfDomicilio.editText?.setText(representante.domicilio)
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
        mostrarSelectorFecha(binding.tfFechaNacimiento.editText as TextInputEditText)
        configurarLimpiezaErrores()

        (binding.tfTipoCedula.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            val adapter = (binding.tfTipoCedula.editText as AutoCompleteTextView).adapter
            val selectedType = adapter.getItem(position) as String
            viewModel.onTipoCedulaSelected(selectedType)

            val cedulaActual = obtenerTexto(binding.tfCedula)
            if (cedulaActual.isNotBlank()) {
                viewModel.validateCedulaRealTime(selectedType, cedulaActual)
            }
        }

        binding.btnRegistrar.setOnClickListener {
            if (args.isEditable) {
                viewModel.onSavePatientClicked(
                    id = args.representanteId,
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
            val dialogTitle = if (args.representanteId == null) "Limpiar campos" else "Restaurar cambios"
            val dialogMessage = if (args.representanteId == null) "¿Desea limpiar todos los campos?" else "¿Desea restaurar los cambios?"
            val positiveButton = if (args.representanteId == null) "Limpiar" else "Restaurar"
            mostrarDialog(requireContext(), dialogTitle, dialogMessage, positiveButton, "No", {
                limpiarCampos()
                if (args.representanteId != null) viewModel.onCreate(args.representanteId, true)
            }, {}, true)
        }

        binding.dropdownEtnias.setOnItemClickListener { _, _, position, _ ->
            viewModel.etnias.value?.get(position)?.let { viewModel.onEtniaSelected(it) }
        }
        binding.dropdownNacionalidades.setOnItemClickListener { _, _, position, _ ->
            viewModel.nacionalidades.value?.get(position)?.let { viewModel.onNacionalidadSelected(it) }
        }
        binding.dropdownEstados.setOnItemClickListener { _, _, position, _ ->
            viewModel.estados.value?.get(position)?.let { viewModel.onEstadoSelected(it) }
        }
        binding.dropdownMunicipios.setOnItemClickListener { _, _, position, _ ->
            viewModel.municipios.value?.get(position)?.let { viewModel.onMunicipioSelected(it) }
        }
        binding.dropdownParroquias.setOnItemClickListener { _, _, position, _ ->
            viewModel.parroquias.value?.get(position)?.let { viewModel.onParroquiaSelected(it) }
        }
    }

    private fun configurarLimpiezaErrores() {
        // Limpiar errores cuando el usuario empieza a escribir
        binding.tfCedula.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfCedula.error = null
        }

        binding.tfTipoCedula.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfTipoCedula.error = null
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
        (binding.tfGenero.editText as? AutoCompleteTextView)?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfGenero.error = null
        }
        (binding.tfEtnia.editText as? AutoCompleteTextView)?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfEtnia.error = null
        }
        (binding.tfNacionalidad.editText as? AutoCompleteTextView)?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfNacionalidad.error = null
        }
        (binding.tfEstado.editText as? AutoCompleteTextView)?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfEstado.error = null
        }
        (binding.tfMunicipio.editText as? AutoCompleteTextView)?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfMunicipio.error = null
        }
        (binding.tfParroquia.editText as? AutoCompleteTextView)?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfParroquia.error = null
        }
        binding.tfDomicilio.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfDomicilio.error = null
        }
        binding.tfPrefijo.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfPrefijo.error = null
        }
        binding.tfTelefono.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfTelefono.error = null
        }
        binding.tfEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tfEmail.error = null
        }

    }


    private fun deshabilitarCampos() {
        listOf(
            binding.dropdownCedulado,
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
        binding.tfTipoCedula.editText?.text = null
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
        binding.tfTipoCedula.error = null
        binding.tfCedula.error = null
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

}