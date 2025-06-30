package com.nutrizulia.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nutrizulia.databinding.FragmentRegistrarPacienteBinding
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nutrizulia.domain.model.catalog.Estado
import com.nutrizulia.domain.model.catalog.Etnia
import com.nutrizulia.domain.model.catalog.Municipio
import com.nutrizulia.domain.model.catalog.Nacionalidad
import com.nutrizulia.domain.model.catalog.Parroquia
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.presentation.viewmodel.RegistrarPacienteViewModel
import com.nutrizulia.util.Utils.generarUUID
import com.nutrizulia.util.Utils.mostrarDialog
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@AndroidEntryPoint
class RegistrarPacienteFragment : Fragment() {

    private val viewModel: RegistrarPacienteViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarPacienteBinding
    private var ultimaFechaSeleccionada: Long? = null
    private var etniaSel: Etnia? = null
    private var nacionalidadSel: Nacionalidad? = null
    private var estadoSel: Estado? = null
    private var municipioSel: Municipio? = null
    private var parroquiaSel: Parroquia? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrarPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarDropdownCedulado()

        mostrarSelectorFecha(binding.tfFechaNacimiento.editText as TextInputEditText)

        viewModel.cargarEtnias()
        viewModel.cargarNacionalidades()
        viewModel.cargarEstados()

        binding.btnLimpiar.setOnClickListener {
            mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Está seguro de limpiar todos los campos?",
                "Sí",
                "No",
                { limpiarCampos() },
                { },
                true
            )
        }

        binding.btnRegistrar.setOnClickListener { registrarPaciente(etniaSel, nacionalidadSel, parroquiaSel) }

        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(binding.root, it) }

        viewModel.errores.observe(viewLifecycleOwner) { mostrarErroresEnCampos(it) }

        viewModel.salir.observe(viewLifecycleOwner) { if (it) findNavController().popBackStack() }

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

    }

    private fun registrarPaciente(etnia: Etnia?, nacionalidad: Nacionalidad?, parroquia: Parroquia?) {
        quitarError()

        // --- 1. Validación de la fecha ---
        val fechaNacimientoStr = obtenerTexto(binding.tfFechaNacimiento)
        if (fechaNacimientoStr.isBlank()) {
            binding.tfFechaNacimiento.error = "La fecha de nacimiento es obligatoria"
            return // Detiene la ejecución si la fecha está vacía
        }

        val fechaNacimiento: LocalDate
        try {
            // --- 2. Intento de conversión seguro ---
            fechaNacimiento = LocalDate.parse(fechaNacimientoStr)
        } catch (e: DateTimeParseException) {
            // Esto se ejecuta si el texto no es una fecha válida (ej. "hola")
            binding.tfFechaNacimiento.error = "El formato de la fecha no es válido"
            return // Detiene la ejecución si el formato es incorrecto
        }

        // Si la validación pasa, continuamos con la creación del objeto
        val nuevoPaciente = Paciente(
            id = generarUUID(),
            usuarioInstitucionId = 0,
            cedula = obtenerTexto(binding.tfTipoCedula) + "-" + obtenerTexto(binding.tfCedula),
            nombres = obtenerTexto(binding.tfNombres),
            apellidos = obtenerTexto(binding.tfApellidos),
            fechaNacimiento = fechaNacimiento, // Usamos la variable ya validada y convertida
            genero = obtenerTexto(binding.tfGenero),
            etniaId = etnia?.id ?: 0,
            nacionalidadId = nacionalidad?.id ?: 0,
            parroquiaId = parroquia?.id ?: 0,
            domicilio = obtenerTexto(binding.tfDomicilio),
            telefono = obtenerTexto(binding.tfPrefijo) + obtenerTexto(binding.tfTelefono),
            correo = obtenerTexto(binding.tfEmail),
            updatedAt = LocalDateTime.now()
        )

        viewModel.registrarPaciente(nuevoPaciente)
    }

    private fun limpiarCampos() {
        quitarError()
        binding.tfTipoCedula.editText?.text = null
        binding.tfCedula.editText?.text = null
        binding.tfEsCedulado.editText?.text = null
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
        binding.tfEmail.editText?.text = null
    }

    private fun quitarError() {
        binding.tfTipoCedula.error = null
        binding.tfCedula.error = null
        binding.tfEsCedulado.error = null
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

            val seleccionInicial = ultimaFechaSeleccionada ?: MaterialDatePicker.todayInUtcMilliseconds()

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
        val dropdown = binding.tfEsCedulado.editText as? AutoCompleteTextView ?: return
        dropdown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.layoutCedulado.visibility = View.VISIBLE
                    binding.layoutNoCeduladoMenorEdad.visibility = View.GONE
                    binding.tfParentesco.editText?.setText("")
                }

                1 -> {
                    binding.layoutNoCeduladoMenorEdad.visibility = View.VISIBLE
                    binding.layoutCedulado.visibility = View.GONE
                    binding.tfCedula.editText?.setText("")
                    binding.tfTipoCedula.editText?.setText("")
                }

                else -> {
                    binding.layoutNoCeduladoMenorEdad.visibility = View.GONE
                    binding.layoutCedulado.visibility = View.GONE
                    binding.tfParentesco.editText?.setText("")
                    binding.tfCedula.editText?.setText("")
                    binding.tfTipoCedula.editText?.setText("")
                }
            }
        }
    }

    private fun mostrarErroresEnCampos(errores: Map<String, String>) {
        val campoMap: Map<String, TextInputLayout> = mapOf(
            "nombres"    to binding.tfNombres,
            "apellidos"  to binding.tfApellidos,
            "fechaNacimiento" to binding.tfFechaNacimiento,
            "genero"          to binding.tfGenero,
            "etnia"           to binding.tfEtnia,
            "nacionalidad"    to binding.tfNacionalidad,
            "estado"          to binding.tfEstado,
            "municipio"       to binding.tfMunicipio,
            "parroquia"       to binding.tfParroquia,
            "correo"          to binding.tfEmail
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