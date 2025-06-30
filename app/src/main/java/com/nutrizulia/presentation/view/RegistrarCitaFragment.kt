package com.nutrizulia.presentation.view

import android.annotation.SuppressLint
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
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.databinding.FragmentRegistrarCitaBinding
import com.nutrizulia.domain.model.catalog.Especialidad
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.presentation.viewmodel.RegistrarCitaViewModel
import com.nutrizulia.util.Utils.calcularEdadDetallada
import com.nutrizulia.util.Utils.generarUUID
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@AndroidEntryPoint
class RegistrarCitaFragment : Fragment() {

    private val viewModel: RegistrarCitaViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarCitaBinding
    private val args: RegistrarCitaFragmentArgs by navArgs()
    private var ultimaFechaSeleccionada: Long? = null
    private var ultimaHora: Int = 12
    private var ultimoMinuto: Int = 0
    private var tipoActividadSel: TipoActividad? = null
    private var especialidadSel: Especialidad? = null
    private var tipoConsultaSel: TipoConsulta? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrarCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mostrarSelectorFecha(binding.tfFechaCita.editText as TextInputEditText)
        mostrarSelectorHora(binding.tfHoraCita.editText as TextInputEditText)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(binding.root, it) }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack(R.id.consultasFragment, false)

        }

        viewModel.cargarTiposActividades()
        binding.dropdownTipoActividad.bind(viewLifecycleOwner, viewModel.tiposActividades,
            toText = { it.nombre   },
            onItemSelected = { et ->
                tipoActividadSel = et
            }
        )

        viewModel.cargarEspecialidades()
        binding.dropdownEspecialidades.bind(viewLifecycleOwner, viewModel.especialidades,
            toText = { it.nombre },
            onItemSelected = { na ->
                especialidadSel = na
            }
        )

        viewModel.cargarTiposConsultas()
        binding.dropdownTipoConsulta.bind(viewLifecycleOwner, viewModel.tiposConsultas,
            toText = { it.displayValue },
            onItemSelected = { na ->
                tipoConsultaSel = na
            }
        )
//        binding.dropdownTipoConsulta.setText(TipoConsulta.PRIMERA_CONSULTA.displayValue, false)

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            quitarErrores()
            errores.forEach { (key, message) ->
                when (key) {
                    "tipoActividad" -> mostrarErrorEnCampo(binding.tfTipoActividad, message)
                    "tipoConsulta" -> mostrarErrorEnCampo(binding.tfTipoConsulta, message)
                    "especialidad" -> mostrarErrorEnCampo(binding.tfEspecialidad, message)
                    "fechaProgramada" -> mostrarErrorEnCampo(binding.tfFechaCita, message)
                }
            }
        }

        viewModel.obtenerPaciente(args.idPaciente)

        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            if (paciente != null) {
                binding.tfNombreCompletoPaciente.editText?.setText("${paciente.nombres} ${paciente.apellidos}")
                binding.tfGeneroPaciente.editText?.setText(paciente.genero)
                val edad = calcularEdadDetallada(paciente.fechaNacimiento)
                binding.tfEdadPaciente.editText?.setText("${edad.anios} años, ${edad.meses} meses y ${edad.dias} días")
            }
        }

        binding.btnRegistrarCita.setOnClickListener {
            registrarCita(args.idPaciente, tipoActividadSel?.id ?: 0, especialidadSel?.id ?: 0, tipoConsultaSel)
        }

        binding.btnLimpiar.setOnClickListener {
            mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Desea limpiar todos los campos?",
                "Sí",
                "No",
                { limpiarCampos() },
                { },
                true
            )
        }

    }

    private fun registrarCita(pacienteId: String, tipoActividadId: Int, especialidadRemitenteId: Int, tipoConsulta: TipoConsulta?) {
        quitarErrores()

        // --- PASO 1: OBTENER Y VALIDAR LA FECHA (LocalDate) ---
        val fechaStr = obtenerTexto(binding.tfFechaCita)
        if (fechaStr.isBlank()) {
            binding.tfFechaCita.error = "La fecha de la cita es obligatoria"
            return
        }

        val fechaCita: LocalDate
        try {
            fechaCita = LocalDate.parse(fechaStr)
        } catch (e: DateTimeParseException) {
            binding.tfFechaCita.error = "El formato de la fecha no es válido"
            return
        }

        // --- PASO 2: OBTENER Y VALIDAR LA HORA (LocalTime) ---
        val horaStr = obtenerTexto(binding.tfHoraCita)
        if (horaStr.isBlank()) {
            binding.tfHoraCita.error = "La hora de la cita es obligatoria"
            return
        }

        val horaCita: LocalTime
        try {
            // El formateador debe coincidir con el texto que genera tu selector de hora (ej: "3:30 PM")
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
            horaCita = LocalTime.parse(horaStr, formatter)
        } catch (e: DateTimeParseException) {
            binding.tfHoraCita.error = "El formato de la hora no es válido"
            return
        }

        // --- PASO 3: COMBINAR LocalDate Y LocalTime ---
        val fechaHoraProgramada: LocalDateTime = fechaCita.atTime(horaCita)

        // --- CREACIÓN DEL OBJETO FINAL ---
        val citaNueva = Consulta(
            id = generarUUID(),
            usuarioInstitucionId = 0,
            pacienteId = pacienteId,
            tipoActividadId = tipoActividadId,
            especialidadRemitenteId = especialidadRemitenteId,
            tipoConsulta = tipoConsulta,
            motivoConsulta = obtenerTexto(binding.tfMotivoConsulta),
            fechaHoraProgramada = fechaHoraProgramada,
            observaciones = null,
            planes = null,
            fechaHoraReal = null,
            estado = Estado.PENDIENTE,
            updatedAt = LocalDateTime.now()
        )

        viewModel.registrarConsulta(citaNueva)
    }

    private fun limpiarCampos() {
        quitarErrores()
        binding.tfTipoActividad.editText?.text?.clear()
        binding.tfEspecialidad.editText?.text?.clear()
        binding.tfTipoConsulta.editText?.text?.clear()
        binding.tfMotivoConsulta.editText?.text?.clear()
        binding.tfFechaCita.editText?.text?.clear()
        binding.tfHoraCita.editText?.text?.clear()
    }

    private fun quitarErrores() {
        binding.tfTipoActividad.error = null
        binding.tfEspecialidad.error = null
        binding.tfTipoConsulta.error = null
        binding.tfEspecialidad.error = null
        binding.tfFechaCita.error = null
        binding.tfHoraCita.error = null
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
                .setValidator(DateValidatorPointForward.now())
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
        binding.tfFechaCita.setStartIconOnClickListener { abrirPicker() }
    }

    private fun mostrarSelectorHora(editText: TextInputEditText) {
        val abrirPicker = {
            val fragmentManager = parentFragmentManager
            val existingPicker = fragmentManager.findFragmentByTag("MaterialTimePicker")
            if (existingPicker != null) {
                fragmentManager.beginTransaction().remove(existingPicker).commit()
            }

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(ultimaHora)
                .setMinute(ultimoMinuto)
                .setTitleText("Selecciona la hora")
                .build()

            picker.addOnPositiveButtonClickListener {
                ultimaHora = picker.hour
                ultimoMinuto = picker.minute

                val hour = ultimaHora
                val minute = ultimoMinuto
                val amPm = if (hour < 12) "AM" else "PM"
                val hourFormatted = if (hour == 0 || hour == 12) 12 else hour % 12
                val minuteFormatted = String.format("%02d", minute)
                val horaFinal = "$hourFormatted:$minuteFormatted $amPm"
                editText.setText(horaFinal)
            }

            picker.show(fragmentManager, "MaterialTimePicker")
        }

        editText.setOnClickListener { abrirPicker() }
        binding.tfHoraCita.setStartIconOnClickListener { abrirPicker() }
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