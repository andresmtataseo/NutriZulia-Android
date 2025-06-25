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
//import com.nutrizulia.domain.model.Comunidad
//import com.nutrizulia.domain.model.Entidad
//import com.nutrizulia.domain.model.Municipio
//import com.nutrizulia.domain.model.Paciente
//import com.nutrizulia.domain.model.Parroquia
//import com.nutrizulia.presentation.viewmodel.EditarPacienteViewModel
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerFechaActual
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class EditarPacienteFragment : Fragment() {

//    private val viewModel: EditarPacienteViewModel by viewModels()
    private lateinit var binding: FragmentEditarPacienteBinding
    private val args: EditarPacienteFragmentArgs by navArgs()
    private var ultimaFechaSeleccionada: Long? = null
//    private var entidadSel: Entidad? = null
//    private var municipioSel: Municipio? = null
//    private var parroquiaSel: Parroquia? = null
//    private var comunidadSel: Comunidad? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditarPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        mostrarSelectorFecha(binding.tfFechaNacimiento.editText as TextInputEditText)
//        viewModel.getPacienteById(args.idPaciente)
//        cargarPaciente()
//        viewModel.cargarEntidades()
//        viewModel.isLoading.observe(viewLifecycleOwner) { binding.progress.visibility = if (it) View.VISIBLE else View.GONE }
//
//        // Entidades → Municipios
//        binding.dropdownEntidades.bind(viewLifecycleOwner, viewModel.entidades,
//            toText = { it.entidad },
//            onItemSelected = { e ->
//                entidadSel = e
//                municipioSel = null; parroquiaSel = null; comunidadSel = null
//                binding.tfMunicipio.editText?.text = null; binding.tfParroquia.editText?.text = null; binding.tfComunidad.editText?.text = null
//                binding.tfMunicipio.visibility = View.VISIBLE; binding.tfParroquia.visibility = View.GONE; binding.tfComunidad.visibility = View.GONE
//                viewModel.cargarMunicipios(e.codEntidad)
//            }
//        )
//
//        // Municipios → Parroquias
//        binding.dropdownMunicipios.bind(viewLifecycleOwner, viewModel.municipios,
//            toText = { it.municipio },
//            onItemSelected = { m ->
//                municipioSel = m
//                parroquiaSel = null; comunidadSel = null
//                binding.tfParroquia.editText?.text = null; binding.tfComunidad.editText?.text = null
//                binding.tfParroquia.visibility = View.VISIBLE; binding.tfComunidad.visibility = View.GONE
//                viewModel.cargarParroquias(entidadSel!!.codEntidad, m.codMunicipio)
//            }
//        )
//
//        // Parroquias → Comunidades
//        binding.dropdownParroquias.bind(viewLifecycleOwner, viewModel.parroquias,
//            toText = { it.parroquia },
//            onItemSelected = { p ->
//                parroquiaSel = p
//                comunidadSel = null
//                binding.tfComunidad.editText?.text = null
//                binding.tfComunidad.visibility = View.VISIBLE
//                viewModel.cargarComunidades(entidadSel!!.codEntidad, municipioSel!!.codMunicipio, p.codParroquia)
//            }
//        )
//
//        // Comunidades
//        binding.dropdownComunidades.bind(viewLifecycleOwner, viewModel.comunidades,
//            toText = { it.nombreComunidad },
//            onItemSelected = { c ->
//                comunidadSel = c
//            }
//        )
//
//        binding.btnDeshacer.setOnClickListener {
//            mostrarDialog(
//                requireContext(),
//                "Advertencia",
//                "¿Está seguro de deshacer los cambios?",
//                "Sí",
//                "No",
//                { cargarPaciente() },
//                { },
//                true
//            )
//        }
//
//        binding.btnGuardarCambios.setOnClickListener {
//            mostrarDialog(
//                requireContext(),
//                "Advertencia",
//                "¿Está seguro de guardar los cambios realizados?",
//                "Sí",
//                "No",
//                { actualizarPaciente(args.idPaciente, entidadSel, municipioSel, parroquiaSel, comunidadSel) },
//                { },
//                true
//            )
//        }
//
//        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(binding.root, it) }
//
//        viewModel.errores.observe(viewLifecycleOwner) { mostrarErroresEnCampos(it) }
//
//        viewModel.salir.observe(viewLifecycleOwner) { if (it) findNavController().popBackStack() }

    }

//    private fun mostrarSelectorFecha(editText: TextInputEditText) {
//        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
//
//        val abrirPicker = {
//            val fragmentManager = parentFragmentManager
//            val existingPicker = fragmentManager.findFragmentByTag("MaterialDatePicker")
//            if (existingPicker != null) {
//                fragmentManager.beginTransaction().remove(existingPicker).commit()
//            }
//
//            val constraints = CalendarConstraints.Builder()
//                .setValidator(DateValidatorPointBackward.now())
//                .build()
//
//            val seleccionInicial = ultimaFechaSeleccionada ?: MaterialDatePicker.todayInUtcMilliseconds()
//
//            val datePicker = MaterialDatePicker.Builder.datePicker()
//                .setTitleText("Selecciona la fecha")
//                .setSelection(seleccionInicial)
//                .setCalendarConstraints(constraints)
//                .build()
//
//            datePicker.addOnPositiveButtonClickListener { utcDate ->
//                ultimaFechaSeleccionada = utcDate
//                editText.setText(dateFormatter.format(utcDate))
//            }
//
//            datePicker.show(fragmentManager, "MaterialDatePicker")
//        }
//
//        editText.setOnClickListener { abrirPicker() }
//        binding.tfFechaNacimiento.setStartIconOnClickListener { abrirPicker() }
//    }
//
//    private fun <T> AutoCompleteTextView.bind(
//        lifecycleOwner: LifecycleOwner,
//        itemsLive: LiveData<List<T>>,
//        toText: (T) -> String,
//        onItemSelected: (T) -> Unit
//    ) {
//        var currentItems: List<T> = emptyList()
//
//        itemsLive.observe(lifecycleOwner) { items ->
//            currentItems = items
//            val names = items.map(toText)
//            val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, names)
//            setAdapter(adapter)
//            if (text.toString() !in names) {
//                setText("", false)
//            }
//        }
//
//        setOnItemClickListener { _, _, position, _ ->
//            onItemSelected(currentItems[position])
//        }
//    }
//
//    private fun mostrarErroresEnCampos(errores: Map<String, String>) {
//        val campoMap: Map<String, TextInputLayout> = mapOf(
//            "primerNombre"    to binding.tfPrimerNombre,
//            "primerApellido"  to binding.tfPrimerApellido,
//            "segundoApellido" to binding.tfSegundoApellido,
//            "fechaNacimiento" to binding.tfFechaNacimiento,
//            "genero"          to binding.tfGenero,
//            "etnia"           to binding.tfEtnia,
//            "nacionalidad"    to binding.tfNacionalidad,
//            "estado"          to binding.tfEstado,
//            "municipio"       to binding.tfMunicipio,
//            "parroquia"       to binding.tfParroquia,
//            "comunidad"       to binding.tfComunidad,
//            "correo"          to binding.tfCorreo
//        )
//
//        errores.forEach { (key, message) ->
//            when (key) {
//                "cedula" -> {
//                    mostrarErrorEnCampo(binding.tfTipoCedula, " ")
//                    mostrarErrorEnCampo(binding.tfCedula, message)
//                }
//                "telefono" -> {
//                    mostrarErrorEnCampo(binding.tfPrefijo, " ")
//                    mostrarErrorEnCampo(binding.tfTelefono, message)
//                }
//                else -> {
//                    campoMap[key]?.let { layout ->
//                        mostrarErrorEnCampo(layout, message)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun quitarError() {
//        binding.tfTipoCedula.error = null
//        binding.tfCedula.error = null
//        binding.tfParentesco.error = null
//        binding.tfPrimerNombre.error = null
//        binding.tfSegundoNombre.error = null
//        binding.tfPrimerApellido.error = null
//        binding.tfSegundoApellido.error = null
//        binding.tfFechaNacimiento.error = null
//        binding.tfGenero.error = null
//        binding.tfEtnia.error = null
//        binding.tfNacionalidad.error = null
//        binding.tfEstado.error = null
//        binding.tfMunicipio.error = null
//        binding.tfParroquia.error = null
//        binding.tfComunidad.error = null
//        binding.tfPrefijo.error = null
//        binding.tfTelefono.error = null
//        binding.tfCorreo.error = null
//    }
//
//    private fun actualizarPaciente(idPaciente: Int, entidad: Entidad?, municipio: Municipio?, parroquia: Parroquia?, comunidad: Comunidad?) {
//        quitarError()
//        val pacienteEditado = Paciente(
//            id = idPaciente,
//            cedula = obtenerTexto(binding.tfTipoCedula) + "-" + obtenerTexto(binding.tfCedula),
//            primerNombre = obtenerTexto(binding.tfPrimerNombre) ?: "",
//            segundoNombre = obtenerTexto(binding.tfSegundoNombre),
//            primerApellido = obtenerTexto(binding.tfPrimerApellido) ?: "",
//            segundoApellido = obtenerTexto(binding.tfSegundoApellido) ?: "",
//            fechaNacimiento = obtenerTexto(binding.tfFechaNacimiento) ?: "",
//            genero = obtenerTexto(binding.tfGenero) ?: "",
//            etnia = obtenerTexto(binding.tfEtnia) ?: "",
//            nacionalidad = obtenerTexto(binding.tfNacionalidad) ?: "",
//            codEntidad = entidad?.codEntidad ?: "",
//            codMunicipio = municipio?.codMunicipio?: "",
//            codParroquia = parroquia?.codParroquia?: "",
//            idComunidad = comunidad?.idComunidad?: "",
//            telefono = obtenerTexto(binding.tfPrefijo) + obtenerTexto(binding.tfTelefono),
//            correo = obtenerTexto(binding.tfCorreo),
//            fechaIngreso = obtenerFechaActual(),
//        )
//        viewModel.actualizarPaciente(pacienteEditado)
//    }
//
//    private fun limpiarCampos() {
//        quitarError()
//        binding.tfTipoCedula.editText?.text = null
//        binding.tfCedula.editText?.text = null
//        binding.tfParentesco.editText?.text = null
//        binding.tfPrimerNombre.editText?.text = null
//        binding.tfSegundoNombre.editText?.text = null
//        binding.tfPrimerApellido.editText?.text = null
//        binding.tfSegundoApellido.editText?.text = null
//        binding.tfFechaNacimiento.editText?.text = null
//        binding.tfGenero.editText?.text = null
//        binding.tfEtnia.editText?.text = null
//        binding.tfNacionalidad.editText?.text = null
//        binding.tfEstado.editText?.text = null
//        binding.tfMunicipio.editText?.text = null
//        binding.tfParroquia.editText?.text = null
//        binding.tfComunidad.editText?.text = null
//        binding.tfPrefijo.editText?.text = null
//        binding.tfTelefono.editText?.text = null
//        binding.tfCorreo.editText?.text = null
//    }
//
//    private fun cargarPaciente() {
//        limpiarCampos()
//        viewModel.paciente.observe(viewLifecycleOwner) {
//            val cedulaParts = it.cedula.split("-")
//            val tipoCedulaEditText = binding.tfTipoCedula.editText as? AutoCompleteTextView
//            tipoCedulaEditText?.setText(cedulaParts[0], false)
//            binding.tfCedula.editText?.setText(cedulaParts[1])
//            binding.tfPrimerNombre.editText?.setText(it.primerNombre)
//            binding.tfSegundoNombre.editText?.setText(it.segundoNombre)
//            binding.tfPrimerApellido.editText?.setText(it.primerApellido)
//            binding.tfSegundoApellido.editText?.setText(it.segundoApellido)
//            binding.tfFechaNacimiento.editText?.setText(it.fechaNacimiento)
//            val generoEditText = binding.tfGenero.editText as? AutoCompleteTextView
//            generoEditText?.setText(it.genero, false)
//            val etniaEditText = binding.tfEtnia.editText as? AutoCompleteTextView
//            etniaEditText?.setText(it.etnia, false)
//            val nacionalidadEditText = binding.tfNacionalidad.editText as? AutoCompleteTextView
//            nacionalidadEditText?.setText(it.nacionalidad, false)
//            val telefonoParts = it.telefono?.split("-")
//            val prefijoEditText = binding.tfPrefijo.editText as? AutoCompleteTextView
//            prefijoEditText?.setText(telefonoParts?.get(0) ?: "", false)
//            binding.tfTelefono.editText?.setText(telefonoParts?.get(1) ?: "")
//            binding.tfCorreo.editText?.setText(it.correo)
//        }
//
//        viewModel.entidad.observe(viewLifecycleOwner) {
//            val entidadEditText = binding.tfEstado.editText as? AutoCompleteTextView
//            entidadEditText?.setText(it.entidad, false)
//            entidadSel = it
//            viewModel.cargarMunicipios(it.codEntidad)
//        }
//        viewModel.municipio.observe(viewLifecycleOwner) {
//            val municipioEditText = binding.tfMunicipio.editText as? AutoCompleteTextView
//            municipioEditText?.setText(it.municipio, false)
//            municipioSel = it
//            viewModel.cargarParroquias(it.codEntidad, it.codMunicipio)
//        }
//        viewModel.parroquia.observe(viewLifecycleOwner) {
//            val parroquiaEditText = binding.tfParroquia.editText as? AutoCompleteTextView
//            parroquiaEditText?.setText(it.parroquia, false)
//            parroquiaSel = it
//            viewModel.cargarComunidades(it.codEntidad, it.codMunicipio, it.codParroquia)
//        }
//        viewModel.comunidad.observe(viewLifecycleOwner) {
//            val comunidadEditText = binding.tfComunidad.editText as? AutoCompleteTextView
//            comunidadEditText?.setText(it.nombreComunidad, false)
//            comunidadSel = it
//        }
//    }

}