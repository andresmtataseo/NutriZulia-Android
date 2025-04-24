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
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.nutrizulia.domain.model.Comunidad
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RegistrarPacienteFragment : Fragment() {

    private val viewModel: RegistrarPacienteViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarPacienteBinding
    private var listaEntidades = listOf<Entidad>()
    private var listaMunicipios = listOf<Municipio>()
    private var listaParroquias = listOf<Parroquia>()
    private var listaComunidades = listOf<Comunidad>()

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

        mostrarSelectorFecha(binding.tfFechaNacimiento.editText as TextInputEditText)

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
            binding.dropdownComunidades.setText("") // limpiar comunidades
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
            binding.dropdownComunidades.setText("") // limpiar comunidades
        }

        // Observa parroquias
        viewModel.parroquias.observe(viewLifecycleOwner) { parroquias ->
            listaParroquias = parroquias
            val nombres = parroquias.map { it.parroquia }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres)
            binding.dropdownParroquias.setAdapter(adapter)
        }

        // Cuando se selecciona una parroquia, carga comunidades
        binding.dropdownParroquias.setOnItemClickListener { _, _, position, _ ->
            val entidad =
                listaEntidades.firstOrNull { it.entidad == binding.dropdownEntidades.text.toString() }
                    ?: listaEntidades.first() // respaldo si falla
            val municipio =
                listaMunicipios.firstOrNull { it.municipio == binding.dropdownMunicipios.text.toString() }
                    ?: listaMunicipios.first() // respaldo si falla
            val parroquia =
                listaParroquias.firstOrNull { it.parroquia == binding.dropdownParroquias.text.toString() }
                    ?: listaParroquias.first() // respaldo si falla
            viewModel.cargarComunidades(entidad.codEntidad, municipio.codMunicipio, parroquia.codParroquia)
        }

        //Observa comunidades
        viewModel.comunidades.observe(viewLifecycleOwner) { comunidades ->
            listaComunidades = comunidades
            val nombres = comunidades.map { it.nombreComunidad }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres)
            binding.dropdownComunidades.setAdapter(adapter)
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
                    "segundoApellido" -> mostrarErrorEnCampo(binding.tfSegundoApellido, message)
                    "fechaNacimiento" -> mostrarErrorEnCampo(binding.tfFechaNacimiento, message)
                    "genero" -> mostrarErrorEnCampo(binding.tfGenero, message)
                    "etnia" -> mostrarErrorEnCampo(binding.tfEtnia, message)
                    "nacionalidad" -> mostrarErrorEnCampo(binding.tfNacionalidad, message)
                    "estado" -> mostrarErrorEnCampo(binding.tfEstado, message)
                    "municipio" -> mostrarErrorEnCampo(binding.tfMunicipio, message)
                    "parroquia" -> mostrarErrorEnCampo(binding.tfParroquia, message)
                    "comunidad" -> mostrarErrorEnCampo(binding.tfComunidad, message)
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
                findNavController().popBackStack()
            // Puedes opcionalmente especificar un destino hasta el cual retroceder
            // findNavController().popBackStack(R.id.destino_anterior, false)
            }
        }
    }

    private fun obtenerIdComunidadSeleccionada(): String? {
        val nombreComunidadeleccionada = binding.dropdownComunidades.text.toString()
        val comunidadEncontrada =
            listaComunidades.find { it.nombreComunidad == nombreComunidadeleccionada }
        return comunidadEncontrada?.idComunidad
    }

    private fun registrarPaciente() {
        quitarError()
        val nuevoPaciente = Paciente(
            id = 0,
            cedula = obtenerTexto(binding.tfTipoCedula) + obtenerTexto(binding.tfCedula),
            primerNombre = obtenerTexto(binding.tfPrimerNombre) ?: "",
            segundoNombre = obtenerTexto(binding.tfsegundoNombre),
            primerApellido = obtenerTexto(binding.tfPrimerApellido) ?: "",
            segundoApellido = obtenerTexto(binding.tfSegundoApellido) ?: "",
            fechaNacimiento = obtenerTexto(binding.tfFechaNacimiento) ?: "",
            genero = obtenerTexto(binding.tfGenero) ?: "",
            etnia = obtenerTexto(binding.tfEtnia) ?: "",
            nacionalidad = obtenerTexto(binding.tfNacionalidad) ?: "",
            codEntidad = listaEntidades.firstOrNull { it.entidad == binding.dropdownEntidades.text.toString() }?.codEntidad ?: "",
            codMunicipio = listaMunicipios.firstOrNull { it.municipio == binding.dropdownMunicipios.text.toString() }?.codMunicipio ?: "",
            codParroquia = listaParroquias.firstOrNull { it.parroquia == binding.dropdownParroquias.text.toString() }?.codParroquia ?: "",
            idComunidad = obtenerIdComunidadSeleccionada().toString(),
            telefono = obtenerTexto(binding.tfPrefijo) + obtenerTexto(binding.tfTelefono),
            correo = obtenerTexto(binding.tfEmail),
            fechaIngreso = obtenerFechaActual(),
        )
        viewModel.registrarPaciente(nuevoPaciente)
    }

    private fun limpiarCampos() {
        binding.tfTipoCedula.editText?.text = null
        binding.tfCedula.editText?.text = null
        binding.tfEsCedulado.editText?.text = null
        binding.tfParentesco.editText?.text = null
        binding.tfPrimerNombre.editText?.text = null
        binding.tfsegundoNombre.editText?.text = null
        binding.tfPrimerApellido.editText?.text = null
        binding.tfSegundoApellido.editText?.text = null
        binding.tfFechaNacimiento.editText?.text = null
        binding.tfGenero.editText?.text = null
        binding.tfEtnia.editText?.text = null
        binding.tfNacionalidad.editText?.text = null
        binding.tfEstado.editText?.text = null
        binding.tfMunicipio.editText?.text = null
        binding.tfParroquia.editText?.text = null
        binding.tfComunidad.editText?.text = null
        binding.tfPrefijo.editText?.text = null
        binding.tfTelefono.editText?.text = null
        binding.tfEmail.editText?.text = null
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
        binding.tfEstado.error = null
        binding.tfMunicipio.error = null
        binding.tfParroquia.error = null
        binding.tfComunidad.error = null
        binding.tfPrefijo.error = null
        binding.tfTelefono.error = null
        binding.tfEmail.error = null
    }

    private var ultimaFechaSeleccionada: Long? = null

    private fun mostrarSelectorFecha(editText: TextInputEditText) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")

        val abrirPicker = {
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

            datePicker.addOnPositiveButtonClickListener { utcDate ->
                ultimaFechaSeleccionada = utcDate
                editText.setText(dateFormatter.format(utcDate))
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


}
