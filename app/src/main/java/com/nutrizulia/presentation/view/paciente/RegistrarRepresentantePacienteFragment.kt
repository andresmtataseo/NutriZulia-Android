package com.nutrizulia.presentation.view.paciente

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nutrizulia.databinding.FragmentRegistrarRepresentantePacienteBinding
import com.nutrizulia.domain.model.catalog.Estado
import com.nutrizulia.domain.model.catalog.Etnia
import com.nutrizulia.domain.model.catalog.Municipio
import com.nutrizulia.domain.model.catalog.Nacionalidad
import com.nutrizulia.domain.model.catalog.Parroquia
import com.nutrizulia.presentation.viewmodel.paciente.RegistrarRepresentantePacienteViewModel
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.getValue

@AndroidEntryPoint
class RegistrarRepresentantePacienteFragment : Fragment() {

    private val viewModel: RegistrarRepresentantePacienteViewModel by viewModels()
    private var _binding: FragmentRegistrarRepresentantePacienteBinding? = null
    private val binding: FragmentRegistrarRepresentantePacienteBinding get() = _binding!!
    private var ultimaFechaSeleccionada: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrarRepresentantePacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate(null, true)
        setupObservers()
        setupListeners()

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

        (binding.tfTipoCedula.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            val adapter = (binding.tfTipoCedula.editText as AutoCompleteTextView).adapter
            val selectedType = adapter.getItem(position) as String
            viewModel.onTipoCedulaSelected(selectedType)
        }

        binding.btnRegistrar.setOnClickListener {
            if (true) {
                viewModel.onSavePatientClicked(
                    id = null,
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
            mostrarDialog(
                requireContext(),
                "Limpiar campos",
                "¿Desea limpiar todos los campos?",
                "Limpiar",
                "No",
                { limpiarCampos() }
            )
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
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
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