package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nutrizulia.databinding.FragmentEditarPacienteBinding
import com.nutrizulia.domain.model.catalog.Estado
import com.nutrizulia.domain.model.catalog.Etnia
import com.nutrizulia.domain.model.catalog.Municipio
import com.nutrizulia.domain.model.catalog.Nacionalidad
import com.nutrizulia.domain.model.catalog.Parroquia
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.presentation.viewmodel.EditarPacienteViewModel
import com.nutrizulia.util.Utils.generarUUID
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerFechaActual
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class EditarPacienteFragment : Fragment() {

    private val viewModel: EditarPacienteViewModel by viewModels()
    private lateinit var binding: FragmentEditarPacienteBinding
    private val args: EditarPacienteFragmentArgs by navArgs()
    private var ultimaFechaSeleccionada: Long? = null
    private var etniaSel: Etnia? = null
    private var nacionalidadSel: Nacionalidad? = null
    private var estadoSel: Estado? = null
    private var municipioSel: Municipio? = null
    private var parroquiaSel: Parroquia? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditarPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mostrarSelectorFecha(binding.tfFechaNacimiento.editText as TextInputEditText)
        viewModel.obtenerPaciente(args.idPaciente)
        cargarPaciente()
        viewModel.cargarEtnias()
        viewModel.cargarNacionalidades()
        viewModel.cargarEstados()
        viewModel.isLoading.observe(viewLifecycleOwner) { binding.progress.visibility = if (it) View.VISIBLE else View.INVISIBLE }

        // Etnias
        binding.dropdownEtnias.bind(viewLifecycleOwner, viewModel.etnias,
            toText = { it.nombre },
            onItemSelected = { et ->
                etniaSel = et
            }
        )

        // Nacionalidades
        binding.dropdownNacionalidades.bind(viewLifecycleOwner, viewModel.nacionalidades,
            toText = { it.nombre },
            onItemSelected = { na ->
                nacionalidadSel = na
            }
        )

        // Estados → Municipios
        binding.dropdownEstados.bind(viewLifecycleOwner, viewModel.estados,
            toText = { it.nombre },
            onItemSelected = { e ->
                estadoSel = e
                municipioSel = null; parroquiaSel = null
                binding.tfMunicipio.editText?.text = null; binding.tfParroquia.editText?.text = null
                binding.tfMunicipio.visibility = View.VISIBLE; binding.tfParroquia.visibility = View.GONE
                viewModel.cargarMunicipios(e.id)
            }
        )

        // Municipios → Parroquias
        binding.dropdownMunicipios.bind(viewLifecycleOwner, viewModel.municipios,
            toText = { it.nombre },
            onItemSelected = { m ->
                municipioSel = m
                parroquiaSel = null
                binding.tfParroquia.editText?.text = null
                binding.tfParroquia.visibility = View.VISIBLE
                viewModel.cargarParroquias(m.id)
            }
        )

        // Parroquias
        binding.dropdownParroquias.bind(viewLifecycleOwner, viewModel.parroquias,
            toText = { it.nombre },
            onItemSelected = { p ->
                parroquiaSel = p
            }
        )

        binding.btnDeshacer.setOnClickListener {
            mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Está seguro de deshacer los cambios?",
                "Sí",
                "No",
                { cargarPaciente() },
                { },
                true
            )
        }

        binding.btnGuardarCambios.setOnClickListener {
            mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Está seguro de guardar los cambios realizados?",
                "Sí",
                "No",
                { actualizarPaciente( args.idPaciente, etniaSel, nacionalidadSel, parroquiaSel) },
                { },
                true
            )
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(binding.root, it) }

        viewModel.errores.observe(viewLifecycleOwner) { mostrarErroresEnCampos(it) }

        viewModel.salir.observe(viewLifecycleOwner) { if (it) findNavController().popBackStack() }

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

    private fun mostrarErroresEnCampos(errores: Map<String, String>) {
        val campoMap: Map<String, TextInputLayout> = mapOf(
            "nombres"    to binding.tfNombres,
            "apellidos" to binding.tfApellidos,
            "fechaNacimiento" to binding.tfFechaNacimiento,
            "genero"          to binding.tfGenero,
            "etnia"           to binding.tfEtnia,
            "nacionalidad"    to binding.tfNacionalidad,
            "estado"          to binding.tfEstado,
            "municipio"       to binding.tfMunicipio,
            "parroquia"       to binding.tfParroquia,
            "correo"          to binding.tfCorreo
        )

        errores.forEach { (key, message) ->
            when (key) {
                "cedula" -> {
                    mostrarErrorEnCampo(binding.tfTipoCedula, " ")
                    mostrarErrorEnCampo(binding.tfCedula, message)
                }
                "telefono" -> {
                    mostrarErrorEnCampo(binding.tfPrefijo, " ")
                    mostrarErrorEnCampo(binding.tfTelefono, message)
                }
                else -> {
                    campoMap[key]?.let { layout ->
                        mostrarErrorEnCampo(layout, message)
                    }
                }
            }
        }
    }

    private fun actualizarPaciente(idPaciente: String, etnia: Etnia?, nacionalidad: Nacionalidad?, parroquia: Parroquia?) {
        quitarError()
        val fechaNacimientoStr = obtenerTexto(binding.tfFechaNacimiento)
        if (fechaNacimientoStr.isBlank()) {
            binding.tfFechaNacimiento.error = "La fecha de nacimiento es obligatoria"
            return
        }

        val fechaNacimiento: LocalDate
        try {
            fechaNacimiento = LocalDate.parse(fechaNacimientoStr)
        } catch (e: DateTimeParseException) {
            binding.tfFechaNacimiento.error = "El formato de la fecha no es válido"
            return
        }

        val nuevoPaciente = Paciente(
            id = idPaciente,
            usuarioInstitucionId = 0,
            cedula = obtenerTexto(binding.tfTipoCedula) + "-" + obtenerTexto(binding.tfCedula),
            nombres = obtenerTexto(binding.tfNombres),
            apellidos = obtenerTexto(binding.tfApellidos),
            fechaNacimiento = fechaNacimiento,
            genero = obtenerTexto(binding.tfGenero),
            etniaId = etnia?.id ?: 0,
            nacionalidadId = nacionalidad?.id ?: 0,
            parroquiaId = parroquia?.id ?: 0,
            domicilio = obtenerTexto(binding.tfDomicilio),
            telefono = obtenerTexto(binding.tfPrefijo) + obtenerTexto(binding.tfTelefono),
            correo = obtenerTexto(binding.tfCorreo),
            updatedAt = LocalDateTime.now()
        )

        viewModel.actualizarPaciente(nuevoPaciente)
    }

    private fun limpiarCampos() {
        quitarError()
        binding.tfTipoCedula.editText?.text = null
        binding.tfCedula.editText?.text = null
        binding.tfParentesco.editText?.text = null
        binding.tfNombres.editText?.text = null
        binding.tfApellidos.editText?.text = null
        binding.tfFechaNacimiento.editText?.text = null
        binding.tfGenero.editText?.text = null
        binding.tfEtnia.editText?.text = null
        binding.tfNacionalidad.editText?.text = null
        binding.tfEstado.editText?.text = null
        binding.tfMunicipio.editText?.text = null
        binding.tfParroquia.editText?.text = null
        binding.tfPrefijo.editText?.text = null
        binding.tfTelefono.editText?.text = null
        binding.tfCorreo.editText?.text = null
    }

    private fun quitarError() {
        binding.tfTipoCedula.error = null
        binding.tfCedula.error = null
        binding.tfParentesco.error = null
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
        binding.tfCorreo.error = null
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

            val seleccionInicial = ultimaFechaSeleccionada ?: MaterialDatePicker.todayInUtcMilliseconds()

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona la fecha")
                .setSelection(seleccionInicial)
                .setCalendarConstraints(constraints)
                .build()

            datePicker.addOnPositiveButtonClickListener { utcDateMillis ->
                ultimaFechaSeleccionada = utcDateMillis

                val localDate = Instant.ofEpochMilli(utcDateMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                editText.setText(localDate.format(dateFormatter))
            }

            datePicker.show(fragmentManager, "MaterialDatePicker")
        }

        editText.setOnClickListener { abrirPicker() }
        binding.tfFechaNacimiento.setStartIconOnClickListener { abrirPicker() }
    }

    private fun cargarPaciente() {
        limpiarCampos()
        viewModel.paciente.observe(viewLifecycleOwner) {
            val cedulaParts = it.cedula.split("-")
            val tipoCedulaEditText = binding.tfTipoCedula.editText as? AutoCompleteTextView
            tipoCedulaEditText?.setText(cedulaParts[0], false)
            binding.tfCedula.editText?.setText(cedulaParts[1])
            binding.tfNombres.editText?.setText(it.nombres)
            binding.tfApellidos.editText?.setText(it.apellidos)
            binding.tfFechaNacimiento.editText?.setText(it.fechaNacimiento.toString())
            val generoEditText = binding.tfGenero.editText as? AutoCompleteTextView
            generoEditText?.setText(it.genero, false)
            val telefonoParts = it.telefono?.split("-")
            val prefijoEditText = binding.tfPrefijo.editText as? AutoCompleteTextView
            prefijoEditText?.setText(telefonoParts?.get(0) ?: "", false)
            binding.tfTelefono.editText?.setText(telefonoParts?.get(1) ?: "")
            binding.tfCorreo.editText?.setText(it.correo)
        }

        viewModel.etnia.observe(viewLifecycleOwner) {
            val etniaEditText = binding.tfEtnia.editText as? AutoCompleteTextView
            etniaEditText?.setText(it.nombre, false)
            etniaSel = it
        }
        viewModel.nacionalidad.observe(viewLifecycleOwner) {
            val nacionalidadEditText = binding.tfNacionalidad.editText as? AutoCompleteTextView
            nacionalidadEditText?.setText(it.nombre, false)
            nacionalidadSel = it
        }
        viewModel.estado.observe(viewLifecycleOwner) {
            val entidadEditText = binding.tfEstado.editText as? AutoCompleteTextView
            entidadEditText?.setText(it.nombre, false)
            estadoSel = it
            viewModel.cargarMunicipios(it.id)
        }
        viewModel.municipio.observe(viewLifecycleOwner) {
            val municipioEditText = binding.tfMunicipio.editText as? AutoCompleteTextView
            municipioEditText?.setText(it.nombre, false)
            municipioSel = it
            viewModel.cargarParroquias(it.id)
        }
        viewModel.parroquia.observe(viewLifecycleOwner) {
            val parroquiaEditText = binding.tfParroquia.editText as? AutoCompleteTextView
            parroquiaEditText?.setText(it.nombre, false)
            parroquiaSel = it
        }

    }

}