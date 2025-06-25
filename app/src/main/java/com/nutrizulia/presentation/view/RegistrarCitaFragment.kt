package com.nutrizulia.presentation.view

import android.annotation.SuppressLint
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.nutrizulia.R
//import com.nutrizulia.presentation.viewmodel.RegistrarCitaViewModel

import com.nutrizulia.databinding.FragmentRegistrarCitaBinding
//import com.nutrizulia.domain.model.Cita
import com.nutrizulia.util.EstadoCita
import com.nutrizulia.util.Utils.calcularEdad
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class RegistrarCitaFragment : Fragment() {

//    private val viewModel: RegistrarCitaViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarCitaBinding
    private val args: RegistrarCitaFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrarCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mostrarSelectorFecha(binding.tfFechaCita.editText as TextInputEditText)
//        mostrarSelectorHora(binding.tfHoraCita.editText as TextInputEditText)
//
//        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
//            mostrarSnackbar(binding.root, mensaje )
//        }
//
//        viewModel.errores.observe(viewLifecycleOwner) { errores ->
//            quitarErrores()
//            errores.forEach { (key, message) ->
//                when (key) {
//                    "tipoCita" -> mostrarErrorEnCampo(binding.tfTipoCita, message)
//                    "especialidad" -> mostrarErrorEnCampo(binding.tfEspecialidad, message)
//                    "fechaProgramada" -> mostrarErrorEnCampo(binding.tfFechaCita, message)
//                    "horaCita" -> {
//                        mostrarErrorEnCampo(binding.tfHoraCita, message)
//                    }
//                }
//            }
//        }
//
//        viewModel.salir.observe(viewLifecycleOwner) { salir ->
//            if (salir) findNavController().popBackStack(R.id.consultasFragment, false)
//
//        }
//
//        viewModel.cargarPaciente(args.idPaciente)
//
//        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
//            if (paciente != null) {
//                binding.tfNombreCompletoPaciente.editText?.setText("${paciente.primerNombre} ${paciente.segundoNombre} ${paciente.primerApellido} ${paciente.segundoApellido}")
//                binding.tfGeneroPaciente.editText?.setText(paciente.genero)
//                binding.tfEdadPaciente.editText?.setText("${calcularEdad(paciente.fechaNacimiento)}")
//            }
//        }
//
//        binding.btnRegistrarCita.setOnClickListener {
//            registrarCita()
//        }
//
//        binding.btnLimpiar.setOnClickListener {
//            mostrarDialog(
//                requireContext(),
//                "Advertencia",
//                "¿Desea limpiar todos los campos?",
//                "Sí",
//                "No",
//                { limpiarCampos() },
//                { },
//                true
//            )
//        }

    }

//    private fun registrarCita() {
//        val citaNueva = Cita(
//            id = 0,
//            usuarioId = 1,
//            pacienteId = args.idPaciente,
//            tipoCita = obtenerTexto(binding.tfTipoCita) ?: "",
//            especialidad = obtenerTexto(binding.tfEspecialidad) ?: "",
//            motivoCita = obtenerTexto(binding.tfMotivoCita),
//            fechaProgramada = obtenerTexto(binding.tfFechaCita) ?: "",
//            horaProgramada = obtenerTexto(binding.tfHoraCita) ?: "",
//            estado = EstadoCita.PENDIENTE.descripcion
//        )
//        viewModel.registrarCita(citaNueva)
//    }
//
//    private fun limpiarCampos() {
//        quitarErrores()
//        binding.tfTipoCita.editText?.text?.clear()
//        binding.tfEspecialidad.editText?.text?.clear()
//        binding.tfMotivoCita.editText?.text?.clear()
//        binding.tfFechaCita.editText?.text?.clear()
//        binding.tfHoraCita.editText?.text?.clear()
//    }
//
//    private fun quitarErrores() {
//        binding.tfTipoCita.error = null
//        binding.tfEspecialidad.error = null
//        binding.tfFechaCita.error = null
//        binding.tfHoraCita.error = null
//    }
//
//    private var ultimaFechaSeleccionada: Long? = null
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
//                .setValidator(DateValidatorPointForward.now())
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
//        binding.tfFechaCita.setStartIconOnClickListener { abrirPicker() }
//    }
//
//
//
//    private var ultimaHora: Int = 12
//    private var ultimoMinuto: Int = 0
//
//    @SuppressLint("DefaultLocale")
//    private fun mostrarSelectorHora(editText: TextInputEditText) {
//        val abrirPicker = {
//            val fragmentManager = parentFragmentManager
//            val existingPicker = fragmentManager.findFragmentByTag("MaterialTimePicker")
//            if (existingPicker != null) {
//                fragmentManager.beginTransaction().remove(existingPicker).commit()
//            }
//
//            val picker = MaterialTimePicker.Builder()
//                .setTimeFormat(TimeFormat.CLOCK_12H)
//                .setHour(ultimaHora)
//                .setMinute(ultimoMinuto)
//                .setTitleText("Selecciona la hora")
//                .build()
//
//            picker.addOnPositiveButtonClickListener {
//                ultimaHora = picker.hour
//                ultimoMinuto = picker.minute
//
//                val hour = ultimaHora
//                val minute = ultimoMinuto
//                val amPm = if (hour < 12) "AM" else "PM"
//                val hourFormatted = if (hour % 12 == 0) 12 else hour % 12
//                val minuteFormatted = String.format("%02d", minute)
//                val horaFinal = "$hourFormatted:$minuteFormatted $amPm"
//                editText.setText(horaFinal)
//            }
//
//            picker.show(fragmentManager, "MaterialTimePicker")
//        }
//
//        editText.setOnClickListener { abrirPicker() }
//        binding.tfHoraCita.setStartIconOnClickListener { abrirPicker() }
//    }



}