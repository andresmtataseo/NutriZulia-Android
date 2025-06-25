package com.nutrizulia.presentation.view

import android.annotation.SuppressLint
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
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
import com.nutrizulia.databinding.FragmentEditarCitaBinding
//import com.nutrizulia.domain.model.Cita
//import com.nutrizulia.presentation.viewmodel.EditarCitaViewModel
import com.nutrizulia.util.EstadoCita
import com.nutrizulia.util.Utils.calcularEdad
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class EditarCitaFragment : Fragment() {

//    private val viewModel: EditarCitaViewModel by viewModels()
    private lateinit var binding: FragmentEditarCitaBinding
    private val args: EditarCitaFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        cargarCitaConPaciente(args.idCita)
//        mostrarSelectorFecha(binding.tfFechaCita.editText as TextInputEditText)
//        mostrarSelectorHora(binding.tfHoraCita.editText as TextInputEditText)
//
//        viewModel.isLoading.observe(viewLifecycleOwner) {
//            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
//        }
//        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(requireView(), it) }
//        viewModel.salir.observe(viewLifecycleOwner) { if (it) findNavController().popBackStack() }
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
//        binding.btnDeshacer.setOnClickListener {
//            mostrarDialog(
//                requireContext(),
//                "Advertencia",
//                "¿Está seguro de deshacer los cambios?",
//                "Sí",
//                "No",
//                { cargarCitaConPaciente(args.idCita) },
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
//                { actualizarCita(args.idCita) },
//                { },
//                true
//            )
//        }

    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun cargarCitaConPaciente(idCita: Int) {
//        limpiarCampos()
//        viewModel.obtenerPaciente(idCita)
//        viewModel.citaConPaciente.observe(viewLifecycleOwner) {
//            binding.tfNombreCompletoPaciente.editText?.setText("${it.paciente.primerNombre} ${it.paciente.segundoNombre} ${it.paciente.primerApellido} ${it.paciente.segundoApellido}")
//            binding.tfEdadPaciente.editText?.setText("${calcularEdad(it.paciente.fechaNacimiento)}")
//            binding.tfGeneroPaciente.editText?.setText(it.paciente.genero)
//
//            val tipoCitaEditText = binding.tfTipoCita.editText as? AutoCompleteTextView
//            tipoCitaEditText?.setText(it.cita.tipoCita, false)
//            val especialidadEditText = binding.tfEspecialidad.editText as? AutoCompleteTextView
//            especialidadEditText?.setText(it.cita.especialidad, false)
//
//            binding.tfMotivoCita.editText?.setText(it.cita.motivoCita)
//            binding.tfFechaCita.editText?.setText(it.cita.fechaProgramada)
//            binding.tfHoraCita.editText?.setText(it.cita.horaProgramada)
//
//        }
//
//    }
//
//    private fun limpiarCampos() {
//        quitarErrores()
//        binding.tfNombreCompletoPaciente.editText?.text = null
//        binding.tfEdadPaciente.editText?.text = null
//        binding.tfGeneroPaciente.editText?.text = null
//        binding.tfTipoCita.editText?.text = null
//        binding.tfEspecialidad.editText?.text = null
//        binding.tfMotivoCita.editText?.text = null
//        binding.tfFechaCita.editText?.text = null
//        binding.tfHoraCita.editText?.text = null
//    }
//
//    private fun actualizarCita(idCita: Int) {
//        val idPaciente = viewModel.citaConPaciente.value?.paciente?.id
//        if (idPaciente != null) {
//            val citaEditada = Cita(
//                id = idCita,
//                usuarioId = 1,
//                pacienteId = idPaciente,
//                tipoCita = binding.tfTipoCita.editText?.text.toString(),
//                especialidad = binding.tfEspecialidad.editText?.text.toString(),
//                motivoCita = binding.tfMotivoCita.editText?.text.toString(),
//                fechaProgramada = binding.tfFechaCita.editText?.text.toString(),
//                horaProgramada = binding.tfHoraCita.editText?.text.toString(),
//                estado = EstadoCita.REPROGRAMADA.descripcion
//            )
//            Log.d("CitaEditada", citaEditada.toString())
//            viewModel.actualizarCita(citaEditada)
//        }
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
//            val seleccionInicial =
//                ultimaFechaSeleccionada ?: MaterialDatePicker.todayInUtcMilliseconds()
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