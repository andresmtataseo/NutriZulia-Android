package com.nutrizulia.presentation.view

import android.app.DatePickerDialog
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
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.presentation.viewmodel.RegistrarPacienteViewModel
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerFechaActual
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RegistrarPacienteFragment : Fragment() {

    private val viewModel: RegistrarPacienteViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarPacienteBinding
    private val fechaNacimientoCalendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrarPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarDropdownCedulado()
        configurarDropdownIdentificarPor()
        configurarFechaNacimiento()

        binding.btnLimpiar.setOnClickListener {
            binding.tfTipoCedula.editText?.setText("")
            binding.tfCedula.editText?.setText("")
            binding.tfEsCedulado.editText?.setText("")
            binding.tfIdentificarPor.editText?.setText("")
            binding.tfParentesco.editText?.setText("")
            binding.tfPartidaNacimiento.editText?.setText("")
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

        binding.btnRegistrar.setOnClickListener {
            registrarPaciente()
        }

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
                    "direccion" -> { mostrarErrorEnCampo(binding.tfEstado, message)
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


    private fun registrarPaciente() {
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
            parroquia = 1,
            telefono = obtenerTexto(binding.tfPrefijo) + obtenerTexto(binding.tfTelefono),
            correo = obtenerTexto(binding.tfEmail),
            fechaIngreso = obtenerFechaActual(),
        )
        viewModel.registrarPaciente(nuevoPaciente)
    }

    private fun configurarDropdownCedulado() = binding.tfEsCedulado.editText?.let { editText ->
        (editText as? AutoCompleteTextView)?.apply {
            val opciones = resources.getStringArray(R.array.tiposIdentificacion)
            setText(opciones.first(), false)
            setOnItemClickListener { parent, _, position, _ ->
                when (parent.getItemAtPosition(position) as String) {
                    opciones[0] -> mostrarLayoutCedulado()
                    opciones[1] -> mostrarLayoutNoCeduladoMenor()
                    else -> ocultarTodosLosLayouts()
                }
            }
        }
    }

    private fun configurarDropdownIdentificarPor() = binding.tfIdentificarPor.editText?.let { editText ->
        (editText as? AutoCompleteTextView)?.apply {
            val opciones = resources.getStringArray(R.array.tiposIdentificacionNoCeduladoMenor)
            setOnItemClickListener { parent, _, position, _ ->
                when (parent.getItemAtPosition(position) as String) {
                    opciones[0] -> mostrarLayoutRepresentante()
                    opciones[1] -> mostrarLayoutPartida()
                    else -> ocultarSubLayoutsNoCedulado()
                }
            }
        }
    }

    private fun configurarFechaNacimiento() = binding.tfFechaNacimiento.editText?.let {
        binding.tfFechaNacimiento.setStartIconOnClickListener { mostrarDatePickerDialog() }
        it.setOnClickListener { mostrarDatePickerDialog() }
    }

    private fun mostrarDatePickerDialog() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                fechaNacimientoCalendar.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                actualizarFechaNacimientoEditText()
            },
            fechaNacimientoCalendar.get(Calendar.YEAR),
            fechaNacimientoCalendar.get(Calendar.MONTH),
            fechaNacimientoCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    private fun actualizarFechaNacimientoEditText() {
        binding.tfFechaNacimiento.editText?.setText(dateFormatter.format(fechaNacimientoCalendar.time))
    }

    private fun mostrarLayoutCedulado() {
        binding.layoutCedulado.visibility = View.VISIBLE
        binding.layoutNoCeduladoMenorEdad.visibility = View.GONE
        ocultarSubLayoutsNoCedulado()
    }

    private fun mostrarLayoutNoCeduladoMenor() {
        binding.layoutCedulado.visibility = View.GONE
        binding.layoutNoCeduladoMenorEdad.visibility = View.VISIBLE
        ocultarSubLayoutsNoCedulado()
    }

    private fun mostrarLayoutRepresentante() {
        binding.layoutNoCeduladoMenorEdadRepresentante.visibility = View.VISIBLE
        binding.layoutNoCeduladoMenorEdadPartida.visibility = View.GONE
    }

    private fun mostrarLayoutPartida() {
        binding.layoutNoCeduladoMenorEdadRepresentante.visibility = View.GONE
        binding.layoutNoCeduladoMenorEdadPartida.visibility = View.VISIBLE
    }

    private fun ocultarTodosLosLayouts() {
        binding.layoutCedulado.visibility = View.GONE
        binding.layoutNoCeduladoMenorEdad.visibility = View.GONE
        ocultarSubLayoutsNoCedulado()
    }

    private fun ocultarSubLayoutsNoCedulado() {
        binding.layoutNoCeduladoMenorEdadRepresentante.visibility = View.GONE
        binding.layoutNoCeduladoMenorEdadPartida.visibility = View.GONE
    }

}
