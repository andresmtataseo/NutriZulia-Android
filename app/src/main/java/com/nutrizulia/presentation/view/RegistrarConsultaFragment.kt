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
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.databinding.FragmentRegistrarConsultaBinding
import com.nutrizulia.domain.model.catalog.Especialidad
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import com.nutrizulia.domain.model.collection.DetallePediatrico
import com.nutrizulia.domain.model.collection.DetalleVital
import com.nutrizulia.presentation.viewmodel.RegistrarConsultaViewModel
import com.nutrizulia.util.Utils.calcularEdad
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@AndroidEntryPoint
class RegistrarConsultaFragment : Fragment() {

    private val viewModel: RegistrarConsultaViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarConsultaBinding
    private val args: RegistrarConsultaFragmentArgs by navArgs()
    private var ultimaFechaSeleccionada: Long? = null
    private var tipoActividadSel: TipoActividad? = null
    private var especialidadSel: Especialidad? = null
    private var tipoConsultaSel: TipoConsulta? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.onCreate(args.idPaciente, args.idConsulta)
        if (args.idConsulta != null) {
            deshabilitarCamposCita()
        } else if (!args.isEditable) {
            deshabilitarCamposCita()
            deshabilitarCamposConsulta()
        }
        if (args.isEditable) {
            setupListeners()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack(R.id.consultasFragment, false)
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            quitarErrores()
            errores.forEach { (key, message) ->
                when (key) {
                    "tipoActividad" -> mostrarErrorEnCampo(binding.tfTipoActividad, message)
                    "tipoConsulta" -> mostrarErrorEnCampo(binding.tfTipoConsulta, message)
                    "especialidad" -> mostrarErrorEnCampo(binding.tfEspecialidad, message)
                }
            }
        }

        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            if (paciente != null) {
                // Mostrar datos
                binding.tfNombreCompletoPaciente.editText?.setText("${paciente.nombres} ${paciente.apellidos}")
                binding.tfGeneroPaciente.editText?.setText(paciente.genero)
                val edad = calcularEdadDetallada(paciente.fechaNacimiento)
                binding.tfEdadPaciente.editText?.setText("${edad.anios} años, ${edad.meses} meses y ${edad.dias} días")
                // Mostrar layout embarazada si es mujer
                if (paciente.genero == "FEMENINO" || paciente.genero == "F") {
                    binding.layoutDetallesEmbarazo.visibility = View.VISIBLE
                } else {
                    binding.layoutDetallesEmbarazo.visibility = View.GONE
                }
                // Mostrar Signos Pediatricos
                val edadCalculada = calcularEdad(paciente.fechaNacimiento)
                if (edadCalculada < 5) {
                    binding.layoutSignosPediatricos.visibility = View.VISIBLE
                } else {
                    binding.layoutSignosPediatricos.visibility = View.GONE
                }
            }
        }
        viewModel.consulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta != null) {
                binding.tfMotivoConsulta.editText?.setText(consulta.motivoConsulta.orEmpty())
            }
        }

        viewModel.tipoActividad.observe(viewLifecycleOwner) { it ->
            val tipoActividadDropdown = binding.tfTipoActividad.editText as? AutoCompleteTextView
            tipoActividadDropdown?.setText(it.nombre, false)
            tipoActividadSel = it
        }

        viewModel.especialidad.observe(viewLifecycleOwner) { it ->
            val especialidadDropdown = binding.tfEspecialidad.editText as? AutoCompleteTextView
            especialidadDropdown?.setText(it.nombre, false)
            especialidadSel = it
        }

        viewModel.tipoConsulta.observe(viewLifecycleOwner) { it ->
            val tipoConsultaDropdown = binding.tfTipoConsulta.editText as? AutoCompleteTextView
            tipoConsultaDropdown?.setText(it.displayValue, false)
            tipoConsultaSel = it
        }
    }

    private fun setupListeners() {

        configurarDropdownEmbarazada()

        mostrarSelectorFecha(binding.tfFechaUltimaMenstruacion.editText as TextInputEditText)

        binding.btnRegistrarConsulta.setOnClickListener {
            registrarConsulta()
        }

        binding.btnLimpiar.setOnClickListener {
            if (args.idConsulta == null) {
                mostrarDialog(
                    requireContext(),
                    "Limpiar campos",
                    "¿Está seguro que desea limpiar todos los campos?",
                    "Limpiar",
                    "No",
                    { limpiarCampos() },
                    { },
                    true
                )
            } else {
                mostrarDialog(
                    requireContext(),
                    "Descartar cambios",
                    "¿Está seguro que desea descartar los cambios?",
                    "Descartar",
                    "No",
                    {
                        limpiarCampos()
                        viewModel.onCreate(args.idPaciente, args.idConsulta)
                    },
                    { },
                    true
                )
            }

        }

        binding.btnAgregarVital.setOnClickListener {
            showVitalDialog()
        }

        binding.btnAgregarMetabolico.setOnClickListener {
            showMetabolicoDialog()
        }

        binding.btnAgregarAntropometrico.setOnClickListener {
            showAntropometricoDialog()
        }

//        binding.btnAgregarObstetricia.setOnClickListener {
//            showObstetriciaDialog()
//        }


        // Dropdowns
        binding.dropdownTipoActividad.bind(
            viewLifecycleOwner, viewModel.tiposActividades,
            toText = { it.nombre },
            onItemSelected = { et ->
                tipoActividadSel = et
            }
        )

        binding.dropdownEspecialidades.bind(
            viewLifecycleOwner, viewModel.especialidades,
            toText = { it.nombre },
            onItemSelected = { na ->
                especialidadSel = na
            }
        )

        binding.dropdownTipoConsulta.bind(
            viewLifecycleOwner, viewModel.tiposConsultas,
            toText = { it.displayValue },
            onItemSelected = { na ->
                tipoConsultaSel = na
            }
        )

        // Signos Vitales
        configurarRemoverCampo(
            binding.btnRemoverTensionArterialSistolica,
            binding.layoutTensionArterialSistolica,
            binding.tfTensionArterialSistolica.editText,
            "Tensión Arterial Sistólica"
        )
        configurarRemoverCampo(
            binding.btnRemoverTensionArterialDiastolica,
            binding.layoutTensionArterialDiastolica,
            binding.tfTensionArterialDiastolica.editText,
            "Tensión Arterial Diastólica"
        )
        configurarRemoverCampo(
            binding.btnRemoverFrecuenciaCardiaca,
            binding.layoutFrecuenciaCardiaca,
            binding.tfFrecuenciaCardiaca.editText,
            "Frecuencia Cardíaca"
        )
        configurarRemoverCampo(
            binding.btnRemoverFrecuenciaRespiratoria,
            binding.layoutFrecuenciaRespiratoria,
            binding.tfFrecuenciaRespiratoria.editText,
            "Frecuencia Respiratoria"
        )
        configurarRemoverCampo(
            binding.btnRemoverTemperatura,
            binding.layoutTemperatura,
            binding.tfTemperatura.editText,
            "Temperatura"
        )
        configurarRemoverCampo(
            binding.btnRemoverSTO2,
            binding.layoutSTO2,
            binding.tfSTO2.editText,
            "Saturación de Oxígeno"
        )
        configurarRemoverCampo(
            binding.btnRemoverPulso,
            binding.layoutPulso,
            binding.tfPulso.editText,
            "Pulso"
        )
        // Signos metabolicos
        configurarRemoverCampo(
            binding.btnRemoverGlicemiaBasal,
            binding.layoutGlicemiaBasal,
            binding.tiGlicemiaBasal,
            "Glicemia Basal"
        )
        configurarRemoverCampo(
            binding.btnRemoverGlicemiaPostprandial,
            binding.layoutGlicemiaPostprandial,
            binding.tiGlicemiaPostprandial,
            "Glicemia Postprandial"
        )
        configurarRemoverCampo(
            binding.btnRemoverGlicemiaAleatoria,
            binding.layoutGlicemiaAleatoria,
            binding.tiGlicemiaAleatoria,
            "Glicemia Aleatoria"
        )
        configurarRemoverCampo(
            binding.btnRemoverHemoglobinaGlicosilada,
            binding.layoutHemoglobinaGlicosilada,
            binding.tiHemoglobinaGlicosilada,
            "Hemoglobina Glicosilada"
        )
        configurarRemoverCampo(
            binding.btnRemoverTrigliceridos,
            binding.layoutTrigliceridos,
            binding.tiTrigliceridos,
            "Triglicéridos"
        )
        configurarRemoverCampo(
            binding.btnRemoverColesterolTotal,
            binding.layoutColesterolTotal,
            binding.tiColesterolTotal,
            "Colesterol Total"
        )
        configurarRemoverCampo(
            binding.btnRemoverColesterolHdl,
            binding.layoutColesterolHdl,
            binding.tiColesterolHdl,
            "Colesterol HDL"
        )
        configurarRemoverCampo(
            binding.btnRemoverColesterolLdl,
            binding.layoutColesterolLdl,
            binding.tiColesterolLdl,
            "Colesterol LDL"
        )
        // Signos antropométricos
        configurarRemoverCampo(
            binding.btnRemoverPeso,
            binding.layoutPeso,
            binding.tfPeso.editText,
            "Peso"
        )
        configurarRemoverCampo(
            binding.btnRemoverAltura,
            binding.layoutAltura,
            binding.tfAltura.editText,
            "Altura (de pie)"
        )
        configurarRemoverCampo(
            binding.btnRemoverTalla,
            binding.layoutTalla,
            binding.tfTalla.editText,
            "Talla (acostado)"
        )
        configurarRemoverCampo(
            binding.btnRemoverCircuferenciaBraquial,
            binding.layoutCircuferenciaBraquial,
            binding.tfCircuferenciaBraquial.editText,
            "Circunferencia Braquial"
        )
        configurarRemoverCampo(
            binding.btnRemoverCircuferenciaCadera,
            binding.layoutCircuferenciaCadera,
            binding.tfCircuferenciaCadera.editText,
            "Circunferencia Cadera"
        )
        configurarRemoverCampo(
            binding.btnRemoverCircuferenciaCintura,
            binding.layoutCircuferenciaCintura,
            binding.tfCircuferenciaCintura.editText,
            "Circunferencia Cintura"
        )
        configurarRemoverCampo(
            binding.btnRemoverPerimetroCefalico,
            binding.layoutPerimetroCefalico,
            binding.tfPerimetroCefalico.editText,
            "Perímetro Cefálico"
        )
        configurarRemoverCampo(
            binding.btnRemoverPliegueTricipital,
            binding.layoutPliegueTricipital,
            binding.tfPliegueTricipital.editText,
            "Pliegue Tricipital"
        )
        configurarRemoverCampo(
            binding.btnRemoverPliegueSubescapular,
            binding.layoutPliegueSubescapular,
            binding.tfPliegueSubescapular.editText,
            "Pliegue Subescapular"
        )

    }

    private fun deshabilitarCamposCita() {
        binding.tfTipoActividad.isEnabled = false
        binding.tfEspecialidad.isEnabled = false
        binding.tfTipoConsulta.isEnabled = false
        binding.tfMotivoConsulta.visibility = View.VISIBLE
        binding.tfMotivoConsulta.isEnabled = false
    }

    private fun deshabilitarCamposConsulta() {
        binding.tfPeso.isEnabled = false
        binding.tfAltura.isEnabled = false
        binding.btnAgregarVital.visibility = View.GONE
        binding.btnLimpiar.visibility = View.GONE
        binding.btnRegistrarConsulta.visibility = View.GONE
    }

    private fun showMetabolicoDialog() {
        val opciones = arrayOf(
            "Glicemia Basal", "Glicemia PostPrandial", "Glicemia Aleatoria",
            "Hemoglobina Glicosilada", "Triglicéridos", "Colesterol Total",
            "Colesterol HDL", "Colesterol LDL"
        )

        val layouts = listOf(
            binding.layoutGlicemiaBasal,
            binding.layoutGlicemiaPostprandial,
            binding.layoutGlicemiaAleatoria,
            binding.layoutHemoglobinaGlicosilada,
            binding.layoutTrigliceridos,
            binding.layoutColesterolTotal,
            binding.layoutColesterolHdl,
            binding.layoutColesterolLdl
        )

        val editTexts = listOf(
            binding.tiGlicemiaBasal,
            binding.tiGlicemiaPostprandial,
            binding.tiGlicemiaAleatoria,
            binding.tiHemoglobinaGlicosilada,
            binding.tiTrigliceridos,
            binding.tiColesterolTotal,
            binding.tiColesterolHdl,
            binding.tiColesterolLdl
        )

        val checksIniciales = layouts.map { it.isVisible }.toBooleanArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona los datos metabólicos")
            .setMultiChoiceItems(opciones, checksIniciales) { _, index, isChecked ->
                layouts[index].isVisible = isChecked
                if (!isChecked) {
                    editTexts[index].setText("")
                }
            }
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun showVitalDialog() {
        val opciones = arrayOf(
            "Tensión arterial sistólica",
            "Tensión arterial diastólica",
            "Frecuencia cardiaca",
            "Frecuencia respiratoria",
            "Temperatura",
            "Saturación de oxígeno",
            "Pulso"
        )

        val layouts = listOf(
            binding.layoutTensionArterialSistolica,
            binding.layoutTensionArterialDiastolica,
            binding.layoutFrecuenciaCardiaca,
            binding.layoutFrecuenciaRespiratoria,
            binding.layoutTemperatura,
            binding.layoutSTO2,
            binding.layoutPulso
        )

        val editTexts = listOf(
            binding.tfTensionArterialSistolica.editText,
            binding.tfTensionArterialDiastolica.editText,
            binding.tfFrecuenciaCardiaca.editText,
            binding.tfFrecuenciaRespiratoria.editText,
            binding.tfTemperatura.editText,
            binding.tfSTO2.editText,
            binding.tfPulso.editText
        )

        val checks = layouts.map { it.isVisible }.toBooleanArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona los signos vitales")
            .setMultiChoiceItems(opciones, checks) { _, index, isChecked ->
                layouts[index].isVisible = isChecked
                if (!isChecked) {
                    editTexts[index]?.setText("")
                }
            }
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun showAntropometricoDialog() {
        val opciones = arrayOf(
            "Peso",
            "Altura (De pie)",
            "Talla (Acostado)",
            "Circuferencia braquial",
            "Circuferencia cadera",
            "Circuferencia cintura",
            "Perímetro cefálico",
            "Pliegue tricipital",
            "Pliegue subescapular"
        )

        val layouts = listOf(
            binding.layoutPeso,
            binding.layoutAltura,
            binding.layoutTalla,
            binding.layoutCircuferenciaBraquial,
            binding.layoutCircuferenciaCadera,
            binding.layoutCircuferenciaCintura,
            binding.layoutPerimetroCefalico,
            binding.layoutPliegueTricipital,
            binding.layoutPliegueSubescapular
        )

        val editTexts = listOf(
            binding.tfPeso.editText,
            binding.tfAltura.editText,
            binding.tfTalla.editText,
            binding.tfCircuferenciaBraquial.editText,
            binding.tfCircuferenciaCadera.editText,
            binding.tfCircuferenciaCintura.editText,
            binding.tfPerimetroCefalico.editText,
            binding.tfPliegueTricipital.editText,
            binding.tfPliegueSubescapular.editText
        )

        val checks = layouts.map { it.isVisible }.toBooleanArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona los datos antropométricos")
            .setMultiChoiceItems(opciones, checks) { _, index, isChecked ->
                layouts[index].isVisible = isChecked
                if (!isChecked) {
                    editTexts[index]?.setText("")
                }
            }
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun registrarConsulta() {
        val ahora = LocalDateTime.now()

        val consulta = crearOActualizarConsulta(ahora)

        if (consulta == null) {
            // Utils.mostrarAlerta(requireContext(), "Error", "No se pudo cargar la consulta existente.")
            return
        }

        val detalleVital = crearDetalleVital(consulta.id, ahora)
        val detalleMetabolico = crearDetalleMetabolico(consulta.id, ahora)
        val detalleAntropometrico = crearDetalleAntropometrico(consulta.id, ahora)

        val esEmbarazo = binding.tfIsEmbarazo.editText?.text.toString().equals("Si", ignoreCase = true)
        val detalleObstetricia = if (esEmbarazo) crearDetalleObstetricia(consulta.id, ahora) else null

        val edadEnAnios = calcularEdad(viewModel.paciente.value?.fechaNacimiento ?: LocalDate.now())
        val esPediatrico = edadEnAnios < 5
        val detallePediatrico = if (esPediatrico) crearDetallePediatrico(consulta.id, ahora) else null

        viewModel.guardarConsulta(
            consulta,
            detalleVital,
            detalleMetabolico,
            detalleAntropometrico,
            detallePediatrico,
            detalleObstetricia
        )
    }

    /**
     * Crea una nueva consulta o actualiza una existente de forma segura.
     * Devuelve null si se intenta actualizar una consulta que no existe en el ViewModel.
     */
    private fun crearOActualizarConsulta(ahora: LocalDateTime): Consulta? {
        val observaciones = obtenerTexto(binding.tfObservaciones)
        val planes = obtenerTexto(binding.tfPlanes)

        return if (args.idConsulta.isNullOrEmpty()) {
            Consulta(
                id = generarUUID(),
                usuarioInstitucionId = viewModel.idUsuarioInstitucion.value ?: 0,
                pacienteId = args.idPaciente,
                tipoActividadId = tipoActividadSel?.id ?: 0,
                especialidadRemitenteId = especialidadSel?.id ?: 0,
                tipoConsulta = tipoConsultaSel,
                motivoConsulta = null,
                fechaHoraProgramada = null,
                observaciones = observaciones,
                planes = planes,
                fechaHoraReal = ahora,
                estado = Estado.SIN_PREVIA_CITA,
                updatedAt = ahora
            )
        } else {
            val consultaExistente = viewModel.consulta.value ?: return null
            consultaExistente.copy(
                observaciones = observaciones,
                planes = planes,
                fechaHoraReal = ahora,
                estado = Estado.COMPLETADA,
                updatedAt = ahora
            )
        }
    }

// --- FUNCIONES AUXILIARES PARA CREAR DETALLES ---

    private fun crearDetalleVital(consultaId: String, ahora: LocalDateTime): DetalleVital {
        return DetalleVital(
            id = generarUUID(),
            consultaId = consultaId,
            tensionArterialSistolica = obtenerTexto(binding.tfTensionArterialSistolica).toIntOrNull(),
            tensionArterialDiastolica = obtenerTexto(binding.tfTensionArterialDiastolica).toIntOrNull(),
            frecuenciaCardiaca = obtenerTexto(binding.tfFrecuenciaCardiaca).toIntOrNull(),
            frecuenciaRespiratoria = obtenerTexto(binding.tfFrecuenciaRespiratoria).toIntOrNull(),
            temperatura = obtenerTexto(binding.tfTemperatura).toDoubleOrNull(),
            saturacionOxigeno = obtenerTexto(binding.tfSTO2).toIntOrNull(),
            pulso = obtenerTexto(binding.tfPulso).toIntOrNull(),
            updatedAt = ahora
        )
    }

    private fun crearDetalleMetabolico(consultaId: String, ahora: LocalDateTime): DetalleMetabolico {
        return DetalleMetabolico(
            id = generarUUID(),
            consultaId = consultaId,
            glicemiaBasal = obtenerTexto(binding.tfGlicemiaBasal).toIntOrNull(),
            glicemiaPostprandial = obtenerTexto(binding.tfGlicemiaPostprandial).toIntOrNull(),
            glicemiaAleatoria = obtenerTexto(binding.tfGlicemiaAleatoria).toIntOrNull(),
            hemoglobinaGlicosilada = obtenerTexto(binding.tfHemoglobinaGlicosilada).toDoubleOrNull(),
            trigliceridos = obtenerTexto(binding.tfTrigliceridos).toIntOrNull(),
            colesterolTotal = obtenerTexto(binding.tfColesterolTotal).toIntOrNull(),
            colesterolHdl = obtenerTexto(binding.tfColesterolHdl).toIntOrNull(),
            colesterolLdl = obtenerTexto(binding.tfColesterolLdl).toIntOrNull(),
            updatedAt = ahora
        )
    }

    private fun crearDetalleAntropometrico(consultaId: String, ahora: LocalDateTime): DetalleAntropometrico {
        return DetalleAntropometrico(
            id = generarUUID(),
            consultaId = consultaId,
            peso = obtenerTexto(binding.tfPeso).toDoubleOrNull(),
            altura = obtenerTexto(binding.tfAltura).toDoubleOrNull(),
            talla = obtenerTexto(binding.tfTalla).toDoubleOrNull(),
            circunferenciaBraquial = obtenerTexto(binding.tfCircuferenciaBraquial).toDoubleOrNull(),
            circunferenciaCadera = obtenerTexto(binding.tfCircuferenciaCadera).toDoubleOrNull(),
            circunferenciaCintura = obtenerTexto(binding.tfCircuferenciaCintura).toDoubleOrNull(),
            perimetroCefalico = obtenerTexto(binding.tfPerimetroCefalico).toDoubleOrNull(),
            pliegueTricipital = obtenerTexto(binding.tfPliegueTricipital).toDoubleOrNull(),
            pliegueSubescapular = obtenerTexto(binding.tfPliegueSubescapular).toDoubleOrNull(),
            updatedAt = ahora
        )
    }

    private fun crearDetalleObstetricia(consultaId: String, ahora: LocalDateTime): DetalleObstetricia {
        val fechaTexto = obtenerTexto(binding.tfFechaUltimaMenstruacion)
        val fechaUltimaMenstruacion = try {
            if (fechaTexto.isNotBlank()) LocalDate.parse(fechaTexto) else null
        } catch (e: DateTimeParseException) {
            null
        }

        return DetalleObstetricia(
            id = generarUUID(),
            consultaId = consultaId,
            estaEmbarazada = true,
            fechaUltimaMenstruacion = fechaUltimaMenstruacion,
            semanasGestacion = obtenerTexto(binding.tfSemanasGestacion).toIntOrNull(),
            pesoPreEmbarazo = obtenerTexto(binding.tfPesoPreEmbarazo).toDoubleOrNull(),
            updatedAt = ahora
        )
    }

    private fun crearDetallePediatrico(consultaId: String, ahora: LocalDateTime): DetallePediatrico {
        return DetallePediatrico(
            id = generarUUID(),
            consultaId = consultaId,
            usaBiberon = null,
            tipoLactancia = null,
            updatedAt = ahora
        )
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

            val seleccionInicial =
                ultimaFechaSeleccionada ?: MaterialDatePicker.todayInUtcMilliseconds()

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
        binding.tfFechaUltimaMenstruacion.setStartIconOnClickListener { abrirPicker() }
    }

    private fun configurarDropdownEmbarazada() {
        val dropdown = binding.tfIsEmbarazo.editText as? AutoCompleteTextView ?: return
        dropdown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.layoutSiEmbarazo.visibility = View.VISIBLE
                }

                1 -> {
                    binding.layoutSiEmbarazo.visibility = View.GONE
                    binding.tfFechaUltimaMenstruacion.editText?.text = null
                    binding.tfSemanasGestacion.editText?.text = null
                    binding.tfPesoPreEmbarazo.editText?.text = null
                }
            }
        }
    }

    private fun quitarErrores() {
        binding.tfDiagnosticoPrincipal.error = null
        binding.tfDiagnosticoSecundario.error = null
    }

    private fun limpiarCampos() {
        quitarErrores()
        binding.tfDiagnosticoPrincipal.editText?.text = null
        binding.tfDiagnosticoSecundario.editText?.text = null

        binding.tfPeso.editText?.text = null
        binding.tfAltura.editText?.text = null
        binding.tfTalla.editText?.text = null
        binding.tfCircuferenciaBraquial.editText?.text = null
        binding.tfCircuferenciaCadera.editText?.text = null
        binding.tfCircuferenciaCintura.editText?.text = null
        binding.tfPerimetroCefalico.editText?.text = null
        binding.tfPliegueTricipital.editText?.text = null
        binding.tfPliegueSubescapular.editText?.text = null

        binding.tfGlicemiaBasal.editText?.text = null
        binding.tfGlicemiaPostprandial.editText?.text = null
        binding.tfGlicemiaAleatoria.editText?.text = null
        binding.tfHemoglobinaGlicosilada.editText?.text = null
        binding.tfTrigliceridos.editText?.text = null
        binding.tfColesterolTotal.editText?.text = null
        binding.tfColesterolHdl.editText?.text = null
        binding.tfColesterolLdl.editText?.text = null

        binding.tfTensionArterialSistolica.editText?.text = null
        binding.tfTensionArterialDiastolica.editText?.text = null
        binding.tfFrecuenciaCardiaca.editText?.text = null
        binding.tfFrecuenciaRespiratoria.editText?.text = null
        binding.tfTemperatura.editText?.text = null
        binding.tfSTO2.editText?.text = null
        binding.tfPulso.editText?.text = null

        binding.tfIsEmbarazo.editText?.text = null
        binding.tfFechaUltimaMenstruacion.editText?.text = null
        binding.tfSemanasGestacion.editText?.text = null
        binding.tfPesoPreEmbarazo.editText?.text = null
    }

    private fun configurarRemoverCampo(
        boton: View,
        layout: View,
        campoTexto: EditText?,
        nombreCampo: String
    ) {
        boton.setOnClickListener {
            mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Desea remover el campo $nombreCampo?",
                "Sí",
                "No",
                {
                    layout.visibility = View.GONE
                    campoTexto?.text = null
                },
                {},
                true
            )
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