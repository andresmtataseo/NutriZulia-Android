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
import com.nutrizulia.domain.model.Entidad
import com.nutrizulia.domain.model.Municipio
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.domain.model.Parroquia
import com.nutrizulia.presentation.viewmodel.RegistrarPacienteViewModel
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerFechaActual
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import android.widget.ArrayAdapter
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RegistrarPacienteFragment : Fragment() {

    private val viewModel: RegistrarPacienteViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarPacienteBinding
    private var listaEntidades = listOf<Entidad>()
    private var listaMunicipios = listOf<Municipio>()
    private var listaParroquias = listOf<Parroquia>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarDropdownCedulado()

        configurarSelectorFecha(binding.tfFechaNacimiento.editText as TextInputEditText)

        viewModel.cargarEntidades()

        // Observa entidades
        viewModel.entidades.observe(viewLifecycleOwner) { entidades ->
            listaEntidades = entidades
            val nombres = entidades.map { it.entidad }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres)
            binding.dropdownEntidades.setAdapter(adapter)
        }

        // Cuando seleccionan una entidad, carga municipios
        binding.dropdownEntidades.setOnItemClickListener { _, _, position, _ ->
            val entidad = listaEntidades[position]
            listaMunicipios = emptyList()
            listaParroquias = emptyList()
            viewModel.cargarMunicipios(entidad.codEntidad)
            binding.dropdownMunicipios.setText("") // limpiar selección previa
            binding.dropdownParroquias.setText("") // limpiar parroquias
        }

        // Observa municipios
        viewModel.municipios.observe(viewLifecycleOwner) { municipios ->
            listaMunicipios = municipios
            val nombres = municipios.map { it.municipio }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres)
            binding.dropdownMunicipios.setAdapter(adapter)
        }

        // Cuando seleccionan un municipio, carga parroquias
        binding.dropdownMunicipios.setOnItemClickListener { _, _, position, _ ->
            val entidad =
                listaEntidades.firstOrNull { it.entidad == binding.dropdownEntidades.text.toString() }
                    ?: listaEntidades.first() // respaldo si falla
            val municipio = listaMunicipios[position]
            listaParroquias = emptyList()
            viewModel.cargarParroquias(entidad.codEntidad, municipio.codMunicipio)
            binding.dropdownParroquias.setText("") // limpiar parroquias anteriores
        }

        // Observa parroquias
        viewModel.parroquias.observe(viewLifecycleOwner) { parroquias ->
            listaParroquias = parroquias
            val nombres = parroquias.map { it.parroquia }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres)
            binding.dropdownParroquias.setAdapter(adapter)
        }

        binding.btnLimpiar.setOnClickListener { limpiarCampos() }

        binding.btnRegistrar.setOnClickListener { registrarPaciente() }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            errores.forEach { (key, message) ->
                when (key) {
                    "cedula" -> {
                        mostrarErrorEnCampo(binding.tfTipoCedula, " ")
                        mostrarErrorEnCampo(binding.tfCedula, message)
                    }

                    "primerNombre" -> mostrarErrorEnCampo(binding.tfPrimerNombre, message)
                    "primerApellido" -> mostrarErrorEnCampo(binding.tfPrimerApellido, message)
                    "fechaNacimiento" -> mostrarErrorEnCampo(binding.tfFechaNacimiento, message)
                    "genero" -> mostrarErrorEnCampo(binding.tfGenero, message)
                    "etnia" -> mostrarErrorEnCampo(binding.tfEtnia, message)
                    "nacionalidad" -> mostrarErrorEnCampo(binding.tfNacionalidad, message)
                    "grupoSanguineo" -> mostrarErrorEnCampo(binding.tfSanguineo, message)
                    "direccion" -> {
                        mostrarErrorEnCampo(binding.tfEstado, message)
                        mostrarErrorEnCampo(binding.tfMunicipio, message)
                        mostrarErrorEnCampo(binding.tfParroquia, message)
                    }

                    "telefono" -> {
                        mostrarErrorEnCampo(binding.tfPrefijo, " ")
                        mostrarErrorEnCampo(binding.tfTelefono, message)
                    }

                    "correo" -> mostrarErrorEnCampo(binding.tfEmail, message)
                }
            }
        }

        viewModel.salir.observe(viewLifecycleOwner) { exitoso ->
            if (exitoso) {
                findNavController().navigate(R.id.action_registrarPacienteFragment_to_pacientesFragment)
            }
        }
    }

    private fun obtenerIdParroquiaSeleccionada(): Int? {
        val nombreParroquiaSeleccionada = binding.dropdownParroquias.text.toString()
        val parroquiaEncontrada =
            listaParroquias.find { it.parroquia == nombreParroquiaSeleccionada }
        return parroquiaEncontrada?.id
    }

    private fun registrarPaciente() {
        quitarError()
        val nuevoPaciente = Paciente(
            id = 0,
            cedula = obtenerTexto(binding.tfTipoCedula) + obtenerTexto(binding.tfCedula),
            primerNombre = obtenerTexto(binding.tfPrimerNombre),
            segundoNombre = obtenerTexto(binding.tfsegundoNombre),
            primerApellido = obtenerTexto(binding.tfPrimerApellido),
            segundoApellido = obtenerTexto(binding.tfSegundoApellido),
            fechaNacimiento = obtenerTexto(binding.tfFechaNacimiento),
            genero = obtenerTexto(binding.tfGenero),
            etnia = obtenerTexto(binding.tfEtnia),
            nacionalidad = obtenerTexto(binding.tfNacionalidad),
            grupoSanguineo = obtenerTexto(binding.tfSanguineo),
            ubicacionId = obtenerIdParroquiaSeleccionada() ?: 0,
            telefono = obtenerTexto(binding.tfPrefijo) + obtenerTexto(binding.tfTelefono),
            correo = obtenerTexto(binding.tfEmail),
            fechaIngreso = obtenerFechaActual(),
        )
        viewModel.registrarPaciente(nuevoPaciente)
    }

    private fun limpiarCampos() {
        binding.tfTipoCedula.editText?.setText("")
        binding.tfCedula.editText?.setText("")
        binding.tfEsCedulado.editText?.setText("")
        binding.tfParentesco.editText?.setText("")
        binding.tfPrimerNombre.editText?.setText("")
        binding.tfsegundoNombre.editText?.setText("")
        binding.tfPrimerApellido.editText?.setText("")
        binding.tfSegundoApellido.editText?.setText("")
        binding.tfFechaNacimiento.editText?.setText("")
        binding.tfGenero.editText?.setText("")
        binding.tfEtnia.editText?.setText("")
        binding.tfNacionalidad.editText?.setText("")
        binding.tfSanguineo.editText?.setText("")
        binding.tfEstado.editText?.setText("")
        binding.tfMunicipio.editText?.setText("")
        binding.tfParroquia.editText?.setText("")
        binding.tfPrefijo.editText?.setText("")
        binding.tfTelefono.editText?.setText("")
        binding.tfEmail.editText?.setText("")
    }

    private fun quitarError() {
        binding.tfTipoCedula.error = null
        binding.tfCedula.error = null
        binding.tfEsCedulado.error = null
        binding.tfParentesco.error = null
        binding.tfPrimerNombre.error = null
        binding.tfsegundoNombre.error = null
        binding.tfPrimerApellido.error = null
        binding.tfSegundoApellido.error = null
        binding.tfFechaNacimiento.error = null
        binding.tfGenero.error = null
        binding.tfEtnia.error = null
        binding.tfNacionalidad.error = null
        binding.tfSanguineo.error = null
        binding.tfEstado.error = null
        binding.tfMunicipio.error = null
        binding.tfParroquia.error = null
        binding.tfPrefijo.error = null
        binding.tfTelefono.error = null
        binding.tfEmail.error = null
    }

    private fun configurarSelectorFecha(editText: TextInputEditText) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona la fecha")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener { utcDate ->
            editText.setText(dateFormatter.format(utcDate))
        }

        val abrirPicker = {
            datePicker.show(parentFragmentManager, "MaterialDatePicker")
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


}
