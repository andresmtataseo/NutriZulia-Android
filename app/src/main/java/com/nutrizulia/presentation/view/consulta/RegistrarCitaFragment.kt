package com.nutrizulia.presentation.view.consulta

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.databinding.FragmentRegistrarCitaBinding
import com.nutrizulia.domain.model.catalog.Especialidad
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.presentation.viewmodel.consulta.RegistrarCitaViewModel
import com.nutrizulia.util.UnavailableDatesValidator
import com.nutrizulia.util.Utils
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
    private var ultimaHora: Int = 8
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
        setupObservers()
        viewModel.onCreate(args.idPaciente, args.idConsulta, args.isEditable)

        binding.dropdownTipoActividad.bind(viewLifecycleOwner, viewModel.tiposActividades,
            toText = { it.nombre },
            onItemSelected = { et -> tipoActividadSel = et }
        )

        binding.dropdownEspecialidades.bind(viewLifecycleOwner, viewModel.especialidades,
            toText = { it.nombre },
            onItemSelected = { na -> especialidadSel = na }
        )
    }

    private fun setupListeners(unavailableDates: List<LocalDate>) {
        mostrarSelectorFecha(binding.tfFechaCita.editText as TextInputEditText, unavailableDates)
        mostrarSelectorHora(binding.tfHoraCita.editText as TextInputEditText)

        binding.btnRegistrarCita.setOnClickListener {
            registrarCita(args.idPaciente, args.idConsulta, tipoActividadSel?.id ?: 0, especialidadSel?.id ?: 0, tipoConsultaSel)
        }

        val isEditing = args.idConsulta != null

        // CAMBIO: Se ajusta el texto del botón según el modo (edición o creación).
        if (isEditing) {
            binding.btnLimpiar.text = "Restaurar"
        }

        binding.btnLimpiar.setOnClickListener {
            val dialogMessage = if (isEditing) "¿Desea restaurar los datos originales de la cita?" else "¿Desea limpiar todos los campos?"
            val positiveAction = if (isEditing) { { restaurarCamposOriginales() } } else { { limpiarCampos() } }

            Utils.mostrarDialog(
                requireContext(),
                "Advertencia",
                dialogMessage,
                "Sí",
                "No",
                positiveAction,
                { },
                true
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        viewModel.fullyBookedDates.observe(viewLifecycleOwner) { dates ->
            if (args.isEditable) {
                setupListeners(dates)
            } else {
                deshabilitarCampos()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            Utils.mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack(R.id.consultasFragment, false)
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            quitarErrores()
            errores.forEach { (key, message) ->
                when (key) {
                    "tipoActividad" -> Utils.mostrarErrorEnCampo(binding.tfTipoActividad, message)
                    "tipoConsulta" -> Utils.mostrarErrorEnCampo(binding.tfTipoConsulta, message)
                    "especialidad" -> Utils.mostrarErrorEnCampo(binding.tfEspecialidad, message)
                    "fechaProgramada" -> Utils.mostrarErrorEnCampo(binding.tfFechaCita, message)
                    "horaProgramada" -> Utils.mostrarErrorEnCampo(binding.tfHoraCita, message)
                }
            }
        }

        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            paciente?.let {
                binding.tfNombreCompletoPaciente.editText?.setText("${it.nombres} ${it.apellidos}")
                binding.tfGeneroPaciente.editText?.setText(it.genero)
                val edad = Utils.calcularEdadDetallada(it.fechaNacimiento)
                binding.tfEdadPaciente.editText?.setText("${edad.anios} años, ${edad.meses} meses y ${edad.dias} días")
            }
        }

        viewModel.consulta.observe(viewLifecycleOwner) { consulta ->
            consulta?.let {
                binding.tfMotivoConsulta.editText?.setText(it.motivoConsulta.orEmpty())
                binding.tfFechaCita.editText?.setText(it.fechaHoraProgramada?.format(
                    DateTimeFormatter.ISO_LOCAL_DATE))
                binding.tfHoraCita.editText?.setText(it.fechaHoraProgramada?.format(
                    DateTimeFormatter.ofPattern("h:mm a", Locale.US)))
                binding.btnRegistrarCita.text = "Reprogramar"
            }
        }

        viewModel.tipoActividad.observe(viewLifecycleOwner) { it ->
            it?.let {
                (binding.tfTipoActividad.editText as? AutoCompleteTextView)?.setText(it.nombre, false)
                tipoActividadSel = it
            }
        }

        viewModel.especialidad.observe(viewLifecycleOwner) { it ->
            it?.let {
                (binding.tfEspecialidad.editText as? AutoCompleteTextView)?.setText(it.nombre, false)
                especialidadSel = it
            }
        }

        viewModel.tipoConsulta.observe(viewLifecycleOwner) { it ->
            it?.let {
                (binding.tfTipoConsulta.editText as? AutoCompleteTextView)?.setText(it.displayValue, false)
                tipoConsultaSel = it
            }
        }
    }

    private fun restaurarCamposOriginales() {
        quitarErrores()
        val consultaOriginal = viewModel.consulta.value ?: return // Salir si no hay datos que restaurar

        // Restaurar campos de texto
        binding.tfMotivoConsulta.editText?.setText(consultaOriginal.motivoConsulta.orEmpty())
        binding.tfFechaCita.editText?.setText(consultaOriginal.fechaHoraProgramada?.format(
            DateTimeFormatter.ISO_LOCAL_DATE))
        binding.tfHoraCita.editText?.setText(consultaOriginal.fechaHoraProgramada?.format(
            DateTimeFormatter.ofPattern("h:mm a", Locale.US)))

        // Restaurar dropdown de Tipo de Actividad
        viewModel.tipoActividad.value?.let {
            (binding.tfTipoActividad.editText as? AutoCompleteTextView)?.setText(it.nombre, false)
            tipoActividadSel = it
        }

        // Restaurar dropdown de Especialidad
        viewModel.especialidad.value?.let {
            (binding.tfEspecialidad.editText as? AutoCompleteTextView)?.setText(it.nombre, false)
            especialidadSel = it
        }

        // Restaurar dropdown de Tipo de Consulta
        viewModel.tipoConsulta.value?.let {
            (binding.tfTipoConsulta.editText as? AutoCompleteTextView)?.setText(it.displayValue, false)
            tipoConsultaSel = it
        }

        Utils.mostrarSnackbar(binding.root, "Cambios revertidos.")
    }

    // ... El resto de la clase permanece igual ...
    private fun registrarCita(pacienteId: String, consultaId: String?, tipoActividadId: Int, especialidadRemitenteId: Int, tipoConsulta: TipoConsulta?) {
        quitarErrores()

        val fechaStr = Utils.obtenerTexto(binding.tfFechaCita)
        if (fechaStr.isBlank()) {
            binding.tfFechaCita.error = "La fecha de la cita es obligatoria"
            return
        }

        val fechaCita: LocalDate = try {
            LocalDate.parse(fechaStr)
        } catch (e: DateTimeParseException) {
            binding.tfFechaCita.error = "El formato de la fecha no es válido"
            return
        }

        val horaStr = Utils.obtenerTexto(binding.tfHoraCita)
        if (horaStr.isBlank()) {
            binding.tfHoraCita.error = "La hora de la cita es obligatoria"
            return
        }

        val horaCita: LocalTime = try {
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
            LocalTime.parse(horaStr, formatter)
        } catch (e: DateTimeParseException) {
            binding.tfHoraCita.error = "El formato de la hora no es válido"
            return
        }

        val fechaHoraProgramada: LocalDateTime = fechaCita.atTime(horaCita)

        val citaNueva = Consulta(
            id = consultaId ?: Utils.generarUUID(),
            usuarioInstitucionId = 0,
            pacienteId = pacienteId,
            tipoActividadId = tipoActividadId,
            especialidadRemitenteId = especialidadRemitenteId,
            tipoConsulta = tipoConsulta,
            motivoConsulta = Utils.obtenerTexto(binding.tfMotivoConsulta),
            fechaHoraProgramada = fechaHoraProgramada,
            observaciones = null,
            planes = null,
            fechaHoraReal = null,
            estado = if (consultaId != null) Estado.REPROGRAMADA else Estado.PENDIENTE,
            updatedAt = LocalDateTime.now(),
            isDeleted = false,
            isSynced = false
        )

        viewModel.guardarConsulta(citaNueva)
    }

    private fun limpiarCampos() {
        quitarErrores()
        binding.tfTipoActividad.editText?.text?.clear()
        binding.tfEspecialidad.editText?.text?.clear()
        binding.tfMotivoConsulta.editText?.text?.clear()
        binding.tfFechaCita.editText?.text?.clear()
        binding.tfHoraCita.editText?.text?.clear()
    }

    private fun quitarErrores() {
        binding.tfTipoActividad.error = null
        binding.tfEspecialidad.error = null
        binding.tfTipoConsulta.error = null
        binding.tfFechaCita.error = null
        binding.tfHoraCita.error = null
    }

    private fun deshabilitarCampos() {
        binding.tfMotivoConsulta.isEnabled = false
        binding.tfFechaCita.isEnabled = false
        binding.tfHoraCita.isEnabled = false
        binding.tfTipoConsulta.isEnabled = false
        binding.btnRegistrarCita.visibility = View.GONE
        binding.btnLimpiar.visibility = View.GONE
        listOf(
            binding.dropdownTipoActividad,
            binding.dropdownEspecialidades
        ).forEach {
            it.isEnabled = false
            (it.parent.parent as? TextInputLayout)?.endIconMode = TextInputLayout.END_ICON_NONE
        }
    }

    private fun mostrarSelectorFecha(editText: TextInputEditText, unavailableDates: List<LocalDate>) {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        fun abrirPicker() {
            val fragmentManager = parentFragmentManager
            val existingPicker = fragmentManager.findFragmentByTag("MaterialDatePicker")
            if (existingPicker != null) {
                fragmentManager.beginTransaction().remove(existingPicker).commit()
            }

            // Convierte las fechas LocalDate a milisegundos UTC
            val unavailableDatesInMillis = unavailableDates.map {
                it.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
            }

            // Crea una lista de validadores para combinarlos
            val validators = listOf(
                DateValidatorPointForward.now(),
                UnavailableDatesValidator(unavailableDatesInMillis)
            )
            val compositeValidator = CompositeDateValidator.allOf(validators)

            val constraints = CalendarConstraints.Builder()
                .setValidator(compositeValidator)
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
            items?.let {
                currentItems = it
                val names = it.map(toText)
                val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, names)
                setAdapter(adapter)
                if (text.toString() !in names) {
                    setText("", false)
                }
            }
        }

        setOnItemClickListener { _, _, position, _ ->
            if (position < currentItems.size) {
                onItemSelected(currentItems[position])
            }
        }
    }
}