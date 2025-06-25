package com.nutrizulia.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentRegistrarPacienteBinding
//import com.nutrizulia.domain.model.Entidad
//import com.nutrizulia.domain.model.Municipio
//import com.nutrizulia.domain.model.Paciente
//import com.nutrizulia.domain.model.Parroquia
//import com.nutrizulia.presentation.viewmodel.RegistrarPacienteViewModel
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerFechaActual
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
//import com.nutrizulia.domain.model.Comunidad
import com.nutrizulia.util.Utils.mostrarDialog
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RegistrarPacienteFragment : Fragment() {

//    private val viewModel: RegistrarPacienteViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarPacienteBinding
    private var ultimaFechaSeleccionada: Long? = null
//    private var entidadSel: Entidad? = null
//    private var municipioSel: Municipio? = null
//    private var parroquiaSel: Parroquia? = null
//    private var comunidadSel: Comunidad? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrarPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        configurarDropdownCedulado()
//
//        mostrarSelectorFecha(binding.tfFechaNacimiento.editText as TextInputEditText)
//
//        viewModel.cargarEntidades()
//
//        binding.btnLimpiar.setOnClickListener {
//            mostrarDialog(
//                requireContext(),
//                "Advertencia",
//                "¿Está seguro de limpiar todos los campos?",
//                "Sí",
//                "No",
//                { limpiarCampos() },
//                { },
//                true
//            )
//        }
//
//        binding.btnRegistrar.setOnClickListener { registrarPaciente(entidadSel, municipioSel, parroquiaSel, comunidadSel) }
//
//        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(binding.root, it) }
//
//        viewModel.errores.observe(viewLifecycleOwner) { mostrarErroresEnCampos(it) }
//
//        viewModel.salir.observe(viewLifecycleOwner) { if (it) findNavController().popBackStack() }
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


    }

//    private fun registrarPaciente(entidad: Entidad?, municipio: Municipio?, parroquia: Parroquia?, comunidad: Comunidad?) {
//        quitarError()
//        val nuevoPaciente = Paciente(
//            id = 0,
//            cedula = obtenerTexto(binding.tfTipoCedula) + "-" + obtenerTexto(binding.tfCedula),
//            primerNombre = obtenerTexto(binding.tfPrimerNombre) ?: "",
//            segundoNombre = obtenerTexto(binding.tfsegundoNombre),
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
//            correo = obtenerTexto(binding.tfEmail),
//            fechaIngreso = obtenerFechaActual(),
//        )
//        viewModel.registrarPaciente(nuevoPaciente)
//    }
//
//    private fun limpiarCampos() {
//        quitarError()
//        binding.tfTipoCedula.editText?.text = null
//        binding.tfCedula.editText?.text = null
//        binding.tfEsCedulado.editText?.text = null
//        binding.tfParentesco.editText?.text = null
//        binding.tfPrimerNombre.editText?.text = null
//        binding.tfsegundoNombre.editText?.text = null
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
//        binding.tfEmail.editText?.text = null
//    }
//
//    private fun quitarError() {
//        binding.tfTipoCedula.error = null
//        binding.tfCedula.error = null
//        binding.tfEsCedulado.error = null
//        binding.tfParentesco.error = null
//        binding.tfPrimerNombre.error = null
//        binding.tfsegundoNombre.error = null
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
//        binding.tfEmail.error = null
//    }
//
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
//
//    private fun configurarDropdownCedulado() {
//        val dropdown = binding.tfEsCedulado.editText as? AutoCompleteTextView ?: return
//        dropdown.setOnItemClickListener { _, _, position, _ ->
//            when (position) {
//                0 -> {
//                    binding.layoutCedulado.visibility = View.VISIBLE
//                    binding.layoutNoCeduladoMenorEdad.visibility = View.GONE
//                    binding.tfParentesco.editText?.setText("")
//                }
//
//                1 -> {
//                    binding.layoutNoCeduladoMenorEdad.visibility = View.VISIBLE
//                    binding.layoutCedulado.visibility = View.GONE
//                    binding.tfCedula.editText?.setText("")
//                    binding.tfTipoCedula.editText?.setText("")
//                }
//
//                else -> {
//                    binding.layoutNoCeduladoMenorEdad.visibility = View.GONE
//                    binding.layoutCedulado.visibility = View.GONE
//                    binding.tfParentesco.editText?.setText("")
//                    binding.tfCedula.editText?.setText("")
//                    binding.tfTipoCedula.editText?.setText("")
//                }
//            }
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
//            "correo"          to binding.tfEmail
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

}