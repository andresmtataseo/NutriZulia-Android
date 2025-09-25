package com.nutrizulia.presentation.view.consulta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.TipoLactancia
import com.nutrizulia.databinding.FragmentRegistrarConsulta2Binding
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import com.nutrizulia.domain.model.collection.DetallePediatrico
import com.nutrizulia.domain.model.collection.DetalleVital
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.presentation.viewmodel.consulta.ConsultaSharedViewModel
import com.nutrizulia.presentation.viewmodel.consulta.DatosClinicosViewModel
import com.nutrizulia.util.ModoConsulta
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@AndroidEntryPoint
class DatosClinicosFragment : Fragment() {

    private val sharedViewModel: ConsultaSharedViewModel by navGraphViewModels(R.id.registrarConsultaGraph) {
        defaultViewModelProviderFactory
    }
    private val clinicalDataViewModel: DatosClinicosViewModel by viewModels()
    private var isHistoria: Boolean = false
    private lateinit var binding: FragmentRegistrarConsulta2Binding
    private var ultimaFechaSeleccionada: Long? = null
    private var wasDataLoaded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarConsulta2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtener el valor de isHistoria desde el SharedViewModel
        isHistoria = sharedViewModel.isHistoria.value ?: false
        val timestamp = System.currentTimeMillis()
        // Observar cambios en isHistoria desde el SharedViewModel
        sharedViewModel.isHistoria.observe(viewLifecycleOwner) { historiaValue ->
            if (isHistoria != historiaValue) {
                isHistoria = historiaValue
                Log.d(
                    "NavFlow",
                    "DatosClinicosFragment: isHistoria actualizado desde SharedViewModel: $isHistoria"
                )
            }
        }

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {

        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading: Boolean ->
            binding.progress.isVisible = isLoading
            binding.content.isVisible = !isLoading
        }

        sharedViewModel.mensaje.observe(viewLifecycleOwner) { message: String ->
            if (message.isNotBlank()) {
                Utils.mostrarSnackbar(binding.root, message)
            }
        }

        sharedViewModel.salir.observe(viewLifecycleOwner) { shouldExit: Boolean ->
            if (shouldExit) findNavController().popBackStack()
        }

        sharedViewModel.modoConsulta.observe(viewLifecycleOwner) { mode: ModoConsulta ->
            when (mode) {
                ModoConsulta.CREAR_SIN_CITA,
                ModoConsulta.CULMINAR_CITA,
                ModoConsulta.EDITAR_CONSULTA -> habilitarCampos()

                ModoConsulta.VER_CONSULTA -> {
                    deshabilitarCamposConsulta()
                    verificarYMostrarMensajesSinDatos()
                }
            }
        }

        sharedViewModel.paciente.observe(viewLifecycleOwner) { paciente: Paciente ->
            val edad: Int = Utils.calcularEdad(paciente.fechaNacimiento)
            // Mostrar detalles de embarazo solo para mujeres mayores de 9 años
            binding.layoutDetallesEmbarazo.isVisible = paciente.genero.equals("FEMENINO", ignoreCase = true) && edad > 9
            // Ocultar signos pediátricos y campo Talla para pacientes de 5 años o más
            binding.layoutSignosPediatricos.isVisible = edad < 5
        }

        // --- Carga de Datos Iniciales ---
        sharedViewModel.consulta.observe(viewLifecycleOwner) { consulta: Consulta? ->
            if (consulta != null && !wasDataLoaded) {
                clinicalDataViewModel.loadClinicalData(consulta.id)
                wasDataLoaded = true
            }
        }

        // --- Transferencia de Datos Iniciales desde clinicalDataViewModel a sharedViewModel ---
        clinicalDataViewModel.initialDetalleVital.observe(viewLifecycleOwner) { initialDetail: DetalleVital? ->
            if (sharedViewModel.detalleVital.value == null) sharedViewModel.updateDetalleVital(
                initialDetail
            )
        }

        clinicalDataViewModel.initialDetalleAntropometrico.observe(viewLifecycleOwner) { initialDetail: DetalleAntropometrico? ->
            if (sharedViewModel.detalleAntropometrico.value == null) sharedViewModel.updateDetalleAntropometrico(
                initialDetail
            )
        }

        clinicalDataViewModel.initialDetalleMetabolico.observe(viewLifecycleOwner) { initialDetail: DetalleMetabolico? ->
            if (sharedViewModel.detalleMetabolico.value == null) sharedViewModel.updateDetalleMetabolico(
                initialDetail
            )
        }

        clinicalDataViewModel.initialDetalleObstetricia.observe(viewLifecycleOwner) { initialDetail: DetalleObstetricia? ->
            if (sharedViewModel.detalleObstetricia.value == null) sharedViewModel.updateDetalleObstetricia(
                initialDetail
            )
        }

        clinicalDataViewModel.initialDetallePediatrico.observe(viewLifecycleOwner) { initialDetail: DetallePediatrico? ->
            if (sharedViewModel.detallePediatrico.value == null) sharedViewModel.updateDetallePediatrico(
                initialDetail
            )
        }

        // --- Observadores de UI (leen desde SharedViewModel) ---
        sharedViewModel.detalleVital.observe(viewLifecycleOwner) { detail: DetalleVital? ->
            detail?.let {
                with(binding) {
                    tfTensionArterialSistolica.editText?.setText(
                        it.tensionArterialSistolica?.toString() ?: ""
                    )
                    layoutTensionArterialSistolica.isVisible = it.tensionArterialSistolica != null
                    tfTensionArterialDiastolica.editText?.setText(
                        it.tensionArterialDiastolica?.toString() ?: ""
                    )
                    layoutTensionArterialDiastolica.isVisible = it.tensionArterialDiastolica != null
                    tfFrecuenciaCardiaca.editText?.setText(it.frecuenciaCardiaca?.toString() ?: "")
                    layoutFrecuenciaCardiaca.isVisible = it.frecuenciaCardiaca != null
                    tfFrecuenciaRespiratoria.editText?.setText(
                        it.frecuenciaRespiratoria?.toString() ?: ""
                    )
                    layoutFrecuenciaRespiratoria.isVisible = it.frecuenciaRespiratoria != null
                    tfTemperatura.editText?.setText(it.temperatura?.toString() ?: "")
                    layoutTemperatura.isVisible = it.temperatura != null
                    tfSTO2.editText?.setText(it.saturacionOxigeno?.toString() ?: "")
                    layoutSTO2.isVisible = it.saturacionOxigeno != null
                    tfPulso.editText?.setText(it.pulso?.toString() ?: "")
                    layoutPulso.isVisible = it.pulso != null
                }
            }
            if (sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) verificarYMostrarMensajesSinDatos()
        }

        sharedViewModel.detalleAntropometrico.observe(viewLifecycleOwner) { detail: DetalleAntropometrico? ->
            detail?.let {
                with(binding) {
                    tfPeso.editText?.setText(it.peso?.toString() ?: "")
                    layoutPeso.isVisible = it.peso != null
                    tfAltura.editText?.setText(it.altura?.toString() ?: "")
                    layoutAltura.isVisible = it.altura != null
                    tfTalla.editText?.setText(it.talla?.toString() ?: "")
                    // Solo mostrar Talla si hay datos Y el paciente es menor de 5 años
                    val paciente = sharedViewModel.paciente.value
                    val edad = paciente?.let { Utils.calcularEdad(it.fechaNacimiento) } ?: 0
                    layoutTalla.isVisible = it.talla != null && edad < 5
                    tfCircuferenciaBraquial.editText?.setText(
                        it.circunferenciaBraquial?.toString() ?: ""
                    )
                    layoutCircuferenciaBraquial.isVisible = it.circunferenciaBraquial != null
                    tfCircuferenciaCadera.editText?.setText(
                        it.circunferenciaCadera?.toString() ?: ""
                    )
                    layoutCircuferenciaCadera.isVisible = it.circunferenciaCadera != null
                    tfCircuferenciaCintura.editText?.setText(
                        it.circunferenciaCintura?.toString() ?: ""
                    )
                    layoutCircuferenciaCintura.isVisible = it.circunferenciaCintura != null
                    tfPerimetroCefalico.editText?.setText(it.perimetroCefalico?.toString() ?: "")
                    layoutPerimetroCefalico.isVisible = it.perimetroCefalico != null
                    tfPliegueTricipital.editText?.setText(it.pliegueTricipital?.toString() ?: "")
                    layoutPliegueTricipital.isVisible = it.pliegueTricipital != null
                    tfPliegueSubescapular.editText?.setText(
                        it.pliegueSubescapular?.toString() ?: ""
                    )
                    layoutPliegueSubescapular.isVisible = it.pliegueSubescapular != null
                }
            }
            if (sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) verificarYMostrarMensajesSinDatos()
        }

        sharedViewModel.detalleMetabolico.observe(viewLifecycleOwner) { detail: DetalleMetabolico? ->
            detail?.let {
                with(binding) {
                    tfGlicemiaBasal.editText?.setText(it.glicemiaBasal?.toString() ?: "")
                    layoutGlicemiaBasal.isVisible = it.glicemiaBasal != null
                    tfGlicemiaPostprandial.editText?.setText(
                        it.glicemiaPostprandial?.toString() ?: ""
                    )
                    layoutGlicemiaPostprandial.isVisible = it.glicemiaPostprandial != null
                    tfGlicemiaAleatoria.editText?.setText(it.glicemiaAleatoria?.toString() ?: "")
                    layoutGlicemiaAleatoria.isVisible = it.glicemiaAleatoria != null
                    tfHemoglobinaGlicosilada.editText?.setText(
                        it.hemoglobinaGlicosilada?.toString() ?: ""
                    )
                    layoutHemoglobinaGlicosilada.isVisible = it.hemoglobinaGlicosilada != null
                    tfTrigliceridos.editText?.setText(it.trigliceridos?.toString() ?: "")
                    layoutTrigliceridos.isVisible = it.trigliceridos != null
                    tfColesterolTotal.editText?.setText(it.colesterolTotal?.toString() ?: "")
                    layoutColesterolTotal.isVisible = it.colesterolTotal != null
                    tfColesterolHdl.editText?.setText(it.colesterolHdl?.toString() ?: "")
                    layoutColesterolHdl.isVisible = it.colesterolHdl != null
                    tfColesterolLdl.editText?.setText(it.colesterolLdl?.toString() ?: "")
                    layoutColesterolLdl.isVisible = it.colesterolLdl != null
                }
            }
            if (sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) verificarYMostrarMensajesSinDatos()
        }

        sharedViewModel.detalleObstetricia.observe(viewLifecycleOwner) { detail: DetalleObstetricia? ->
            detail?.let { it ->
                Log.d("detalleObstetricia", it.toString())

                // Manejo de embarazo
                val embarazo = if (it.estaEmbarazada == true) "Si" else "No"
                (binding.tfIsEmbarazo.editText as? AutoCompleteTextView)?.setText(embarazo, false)

                binding.tfIsEmbarazo.isVisible = it.estaEmbarazada != null
                binding.layoutSiEmbarazo.isVisible = it.estaEmbarazada == true

                // Si está embarazada, mostrar campos relacionados
                if (it.estaEmbarazada == true) {
                    // Fecha última menstruación
                    val fechaUltimaMenstruacion = it.fechaUltimaMenstruacion?.toString() ?: ""
                    binding.tfFechaUltimaMenstruacion.editText?.setText(fechaUltimaMenstruacion)
                    binding.tfFechaUltimaMenstruacion.isVisible = it.fechaUltimaMenstruacion != null

                    // Semanas de gestación
                    val semanasGestacion = it.semanasGestacion?.toString() ?: ""
                    binding.tfSemanasGestacion.editText?.setText(semanasGestacion)
                    binding.tfSemanasGestacion.isVisible = it.semanasGestacion != null

                    // Peso pre-embarazo
                    val pesoPreEmbarazo = it.pesoPreEmbarazo?.toString() ?: ""
                    binding.tfPesoPreEmbarazo.editText?.setText(pesoPreEmbarazo)
                    binding.tfPesoPreEmbarazo.isVisible = it.pesoPreEmbarazo != null
                }
            }

            if (sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                verificarYMostrarMensajesSinDatos()
            }
        }


        sharedViewModel.detallePediatrico.observe(viewLifecycleOwner) { detail: DetallePediatrico? ->
            detail?.let {
                with(binding) {
                    if (it.usaBiberon != null) {
                        val biberonTexto: String = if (it.usaBiberon == true) "Si" else "No"
                        (tfUsaBiberon.editText as? AutoCompleteTextView)?.setText(
                            biberonTexto,
                            false
                        )
                        tfUsaBiberon.isVisible = true
                    }
                    if (it.tipoLactancia != null) {
                        (tfTipoLactancia.editText as? AutoCompleteTextView)?.setText(
                            it.tipoLactancia.name,
                            false
                        )
                        tfTipoLactancia.isVisible = true
                    }
                }
            }
            if (sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) verificarYMostrarMensajesSinDatos()
        }
    }

    private fun setupListeners() {
        configurarDropdownEmbarazada()
        configurarDropdownPediatricos()
        mostrarSelectorFecha(binding.tfFechaUltimaMenstruacion.editText as TextInputEditText)

        binding.btnSiguiente.setOnClickListener {
            val consultaId: String =
                sharedViewModel.consulta.value?.id ?: sharedViewModel.consultaEditando.value?.id
                ?: run {
                    Log.d("datosClinicos", "consulta" + sharedViewModel.consulta.value.toString())
                    Log.d(
                        "datosClinicos",
                        "consultaEdit" + sharedViewModel.consultaEditando.value.toString()
                    )
                    Utils.mostrarSnackbar(
                        binding.root,
                        "Error: No se ha podido identificar la consulta."
                    )
                    return@setOnClickListener
                }
            Log.d("idConsulta", "id = " + consultaId)

            // Crear y actualizar Detalle Vital
            val detalleVital: DetalleVital? = clinicalDataViewModel.createDetalleVital(
                idConsulta = consultaId,
                existingId = sharedViewModel.detalleVital.value?.id,
                frecuenciaCardiaca = binding.tfFrecuenciaCardiaca.editText?.text.toString()
                    .toIntOrNull(),
                presionSistolica = binding.tfTensionArterialSistolica.editText?.text.toString()
                    .toIntOrNull(),
                presionDiastolica = binding.tfTensionArterialDiastolica.editText?.text.toString()
                    .toIntOrNull(),
                frecuenciaRespiratoria = binding.tfFrecuenciaRespiratoria.editText?.text.toString()
                    .toIntOrNull(),
                temperatura = binding.tfTemperatura.editText?.text.toString().toDoubleOrNull(),
                saturacionOxigeno = binding.tfSTO2.editText?.text.toString().toIntOrNull(),
                pulso = binding.tfPulso.editText?.text.toString().toIntOrNull()
            )
            sharedViewModel.updateDetalleVital(detalleVital)

            // Crear y actualizar Detalle Antropométrico
            val detalleAntropometrico: DetalleAntropometrico? =
                clinicalDataViewModel.createDetalleAntropometrico(
                    idConsulta = consultaId,
                    existingId = sharedViewModel.detalleAntropometrico.value?.id,
                    peso = binding.tfPeso.editText?.text.toString().toDoubleOrNull(),
                    altura = binding.tfAltura.editText?.text.toString().toDoubleOrNull(),
                    talla = binding.tfTalla.editText?.text.toString().toDoubleOrNull(),
                    circunferenciaBraquial = binding.tfCircuferenciaBraquial.editText?.text.toString()
                        .toDoubleOrNull(),
                    circunferenciaCadera = binding.tfCircuferenciaCadera.editText?.text.toString()
                        .toDoubleOrNull(),
                    circunferenciaCintura = binding.tfCircuferenciaCintura.editText?.text.toString()
                        .toDoubleOrNull(),
                    perimetroCefalico = binding.tfPerimetroCefalico.editText?.text.toString()
                        .toDoubleOrNull(),
                    pliegueTricipital = binding.tfPliegueTricipital.editText?.text.toString()
                        .toDoubleOrNull(),
                    pliegueSubescapular = binding.tfPliegueSubescapular.editText?.text.toString()
                        .toDoubleOrNull()
                )
            sharedViewModel.updateDetalleAntropometrico(detalleAntropometrico)

            // Crear y actualizar Detalle Metabólico
            val detalleMetabolico: DetalleMetabolico? =
                clinicalDataViewModel.createDetalleMetabolico(
                    idConsulta = consultaId,
                    existingId = sharedViewModel.detalleMetabolico.value?.id,
                    glicemiaBasal = binding.tfGlicemiaBasal.editText?.text.toString().toIntOrNull(),
                    glicemiaPostprandial = binding.tfGlicemiaPostprandial.editText?.text.toString()
                        .toIntOrNull(),
                    glicemiaAleatoria = binding.tfGlicemiaAleatoria.editText?.text.toString()
                        .toIntOrNull(),
                    hemoglobinaGlicosilada = binding.tfHemoglobinaGlicosilada.editText?.text.toString()
                        .toDoubleOrNull(),
                    trigliceridos = binding.tfTrigliceridos.editText?.text.toString().toIntOrNull(),
                    colesterolTotal = binding.tfColesterolTotal.editText?.text.toString()
                        .toIntOrNull(),
                    colesterolHdl = binding.tfColesterolHdl.editText?.text.toString().toIntOrNull(),
                    colesterolLdl = binding.tfColesterolLdl.editText?.text.toString().toIntOrNull()
                )
            sharedViewModel.updateDetalleMetabolico(detalleMetabolico)

            // Crear y actualizar Detalle Obstétrico
            val estaEmbarazada: Boolean? =
                when (binding.tfIsEmbarazo.editText?.text.toString().lowercase()) {
                    "si" -> true
                    "no" -> false
                    else -> null
                }
            val fechaUltimaMenstruacion: LocalDate? = try {
                LocalDate.parse(binding.tfFechaUltimaMenstruacion.editText?.text.toString())
            } catch (e: DateTimeParseException) {
                null
            }
            val detalleObstetricia: DetalleObstetricia? =
                clinicalDataViewModel.createDetalleObstetricia(
                    idConsulta = consultaId,
                    existingId = sharedViewModel.detalleObstetricia.value?.id,
                    estaEmbarazada = estaEmbarazada,
                    fechaUltimaMenstruacion = fechaUltimaMenstruacion,
                    semanasGestacion = binding.tfSemanasGestacion.editText?.text.toString()
                        .toIntOrNull(),
                    pesoPreEmbarazo = binding.tfPesoPreEmbarazo.editText?.text.toString()
                        .toDoubleOrNull()
                )
            sharedViewModel.updateDetalleObstetricia(detalleObstetricia)

            // Crear y actualizar Detalle Pediátrico
            val usaBiberon: Boolean? =
                when (binding.tfUsaBiberon.editText?.text.toString().lowercase()) {
                    "si" -> true
                    "no" -> false
                    else -> null
                }
            val tipoLactanciaStr: String = binding.tfTipoLactancia.editText?.text.toString()
            val tipoLactancia: TipoLactancia? =
                TipoLactancia.entries.find { it.name.equals(tipoLactanciaStr, ignoreCase = true) }
            val detallePediatrico: DetallePediatrico? =
                clinicalDataViewModel.createDetallePediatrico(
                    idConsulta = consultaId,
                    existingId = sharedViewModel.detallePediatrico.value?.id,
                    usaBiberon = usaBiberon,
                    tipoLactancia = tipoLactancia
                )
            if (detallePediatrico != null) {
                sharedViewModel.updateDetallePediatrico(detallePediatrico)
            }

            findNavController().navigate(
                DatosClinicosFragmentDirections.actionRegistrarConsulta2FragmentToRegistrarConsulta3Fragment()
            )
            val timestamp = System.currentTimeMillis()
            // Pasar el argumento isHistoria al siguiente fragmento
            findNavController().currentBackStackEntry?.arguments?.putBoolean(
                "isHistoria",
                isHistoria
            )
            Log.d(
                "NavFlow",
                "DatosClinicosFragment: Navegando a EvaluacionesFinalesFragment con isHistoria=$isHistoria | timestamp=$timestamp"
            )
        }

        binding.btnLimpiar.setOnClickListener {
            Utils.mostrarDialog(
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

        binding.btnAgregarVital.setOnClickListener { showVitalDialog() }
        binding.btnAgregarMetabolico.setOnClickListener { showMetabolicoDialog() }
        binding.btnAgregarAntropometrico.setOnClickListener { showAntropometricoDialog() }
        binding.btnAgregarPediatrico.setOnClickListener { showPediatricoDialog() }

        // --- Listeners para remover campos (Corregidos) ---
        // Signos Vitales
        configurarRemoverCampo(
            binding.btnRemoverTensionArterialSistolica,
            binding.layoutTensionArterialSistolica,
            binding.tfTensionArterialSistolica.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverTensionArterialDiastolica,
            binding.layoutTensionArterialDiastolica,
            binding.tfTensionArterialDiastolica.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverFrecuenciaCardiaca,
            binding.layoutFrecuenciaCardiaca,
            binding.tfFrecuenciaCardiaca.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverFrecuenciaRespiratoria,
            binding.layoutFrecuenciaRespiratoria,
            binding.tfFrecuenciaRespiratoria.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverTemperatura,
            binding.layoutTemperatura,
            binding.tfTemperatura.editText
        )
        configurarRemoverCampo(binding.btnRemoverSTO2, binding.layoutSTO2, binding.tfSTO2.editText)
        configurarRemoverCampo(
            binding.btnRemoverPulso,
            binding.layoutPulso,
            binding.tfPulso.editText
        )
        // Signos Metabólicos
        configurarRemoverCampo(
            binding.btnRemoverGlicemiaBasal,
            binding.layoutGlicemiaBasal,
            binding.tfGlicemiaBasal.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverGlicemiaPostprandial,
            binding.layoutGlicemiaPostprandial,
            binding.tfGlicemiaPostprandial.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverGlicemiaAleatoria,
            binding.layoutGlicemiaAleatoria,
            binding.tfGlicemiaAleatoria.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverHemoglobinaGlicosilada,
            binding.layoutHemoglobinaGlicosilada,
            binding.tfHemoglobinaGlicosilada.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverTrigliceridos,
            binding.layoutTrigliceridos,
            binding.tfTrigliceridos.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverColesterolTotal,
            binding.layoutColesterolTotal,
            binding.tfColesterolTotal.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverColesterolHdl,
            binding.layoutColesterolHdl,
            binding.tfColesterolHdl.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverColesterolLdl,
            binding.layoutColesterolLdl,
            binding.tfColesterolLdl.editText
        )
        // Signos Antropométricos
        configurarRemoverCampo(binding.btnRemoverPeso, binding.layoutPeso, binding.tfPeso.editText)
        configurarRemoverCampo(
            binding.btnRemoverAltura,
            binding.layoutAltura,
            binding.tfAltura.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverTalla,
            binding.layoutTalla,
            binding.tfTalla.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverCircuferenciaBraquial,
            binding.layoutCircuferenciaBraquial,
            binding.tfCircuferenciaBraquial.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverCircuferenciaCadera,
            binding.layoutCircuferenciaCadera,
            binding.tfCircuferenciaCadera.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverCircuferenciaCintura,
            binding.layoutCircuferenciaCintura,
            binding.tfCircuferenciaCintura.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverPerimetroCefalico,
            binding.layoutPerimetroCefalico,
            binding.tfPerimetroCefalico.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverPliegueTricipital,
            binding.layoutPliegueTricipital,
            binding.tfPliegueTricipital.editText
        )
        configurarRemoverCampo(
            binding.btnRemoverPliegueSubescapular,
            binding.layoutPliegueSubescapular,
            binding.tfPliegueSubescapular.editText
        )
    }

    private fun configurarDropdownPediatricos() {
        val biberonItems: List<String> = listOf("Si", "No")
        val biberonAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.list_item, biberonItems)
        (binding.tfUsaBiberon.editText as? AutoCompleteTextView)?.setAdapter(biberonAdapter)

        val lactanciaItems: List<String> = TipoLactancia.entries.map { it.name }
        val lactanciaAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.list_item, lactanciaItems)
        (binding.tfTipoLactancia.editText as? AutoCompleteTextView)?.setAdapter(lactanciaAdapter)
    }

    private fun configurarRemoverCampo(
        boton: View,
        layout: View,
        campoTexto: EditText?,
        nombreCampo: String = ""
    ) {
        boton.setOnClickListener {
            Utils.mostrarDialog(
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

    private fun deshabilitarCamposConsulta() {
        // Antropométricos
        binding.tiPeso.isEnabled = false
        binding.tiAltura.isEnabled = false
        binding.tiTalla.isEnabled = false
        binding.tiCircuferenciaBraquial.isEnabled = false
        binding.tiCircuferenciaCadera.isEnabled = false
        binding.tiCircuferenciaCintura.isEnabled = false
        binding.tiPerimetroCefalico.isEnabled = false
        binding.tiPliegueTricipital.isEnabled = false
        binding.tiPliegueSubescapular.isEnabled = false
        binding.btnAgregarAntropometrico.visibility = View.GONE
        // Vitales
        binding.tiTensionArterialSistolica.isEnabled = false
        binding.tiTensionArterialDiastolica.isEnabled = false
        binding.tiFrecuenciaCardiaca.isEnabled = false
        binding.tiFrecuenciaRespiratoria.isEnabled = false
        binding.tiTemperatura.isEnabled = false
        binding.tiSTO2.isEnabled = false
        binding.tiPulso.isEnabled = false
        binding.btnAgregarVital.visibility = View.GONE
        // Metabólicos
        binding.tiGlicemiaBasal.isEnabled = false
        binding.tiGlicemiaPostprandial.isEnabled = false
        binding.tiGlicemiaAleatoria.isEnabled = false
        binding.tiHemoglobinaGlicosilada.isEnabled = false
        binding.tiTrigliceridos.isEnabled = false
        binding.tiColesterolTotal.isEnabled = false
        binding.tiColesterolHdl.isEnabled = false
        binding.tiColesterolLdl.isEnabled = false
        binding.btnAgregarMetabolico.visibility = View.GONE
        // Pediátrico
        binding.tfUsaBiberon.isEnabled = false
        binding.tfTipoLactancia.isEnabled = false
        binding.btnAgregarPediatrico.visibility = View.GONE
        // Obstetricia
        binding.tfIsEmbarazo.isEnabled = false
        binding.tfFechaUltimaMenstruacion.isEnabled = false
        binding.tfSemanasGestacion.isEnabled = false
        binding.tfPesoPreEmbarazo.isEnabled = false

        binding.btnLimpiar.visibility = View.GONE
        ocultarBotonesRemoverCampo()
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

    private fun showPediatricoDialog() {
        val opciones = arrayOf(
            "¿Usa biberón?",
            "Tipo de lactancia"
        )

        val layouts = listOf(
            binding.tfUsaBiberon,
            binding.tfTipoLactancia
        )

        val dropdowns = listOf(
            binding.dropdownUsaBiberon,
            binding.dropdownTipoLactancia
        )

        val checks = layouts.map { it.isVisible }.toBooleanArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona los datos pediátricos")
            .setMultiChoiceItems(opciones, checks) { _, index, isChecked ->
                layouts[index].isVisible = isChecked
                if (!isChecked) {
                    dropdowns[index].setText("", false)
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
        // Obtener la edad del paciente para filtrar campos según la edad
        val edad = sharedViewModel.paciente.value?.fechaNacimiento?.let {
            Utils.calcularEdad(it)
        } ?: 0

        // Lista completa de opciones
        val opcionesCompletas = arrayOf(
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

        val layoutsCompletos = listOf(
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

        val editTextsCompletos = listOf(
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

        // Filtrar opciones según la edad (excluir Talla para pacientes >= 5 años)
        val indicesValidos = opcionesCompletas.indices.filter { index ->
            if (index == 2) { // Índice de "Talla (Acostado)"
                edad < 5 // Solo mostrar si el paciente es menor de 5 años
            } else {
                true // Mostrar todas las demás opciones
            }
        }

        val opciones = indicesValidos.map { opcionesCompletas[it] }.toTypedArray()
        val layouts = indicesValidos.map { layoutsCompletos[it] }
        val editTexts = indicesValidos.map { editTextsCompletos[it] }

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

    private fun configurarDropdownEmbarazada() {
        val dropdown = binding.tfIsEmbarazo.editText as? AutoCompleteTextView ?: return
        dropdown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    // Mostrar todos los campos de embarazo cuando se selecciona "Sí"
                    binding.layoutSiEmbarazo.visibility = View.VISIBLE
                    binding.tfFechaUltimaMenstruacion.visibility = View.VISIBLE
                    binding.tfSemanasGestacion.visibility = View.VISIBLE
                    binding.tfPesoPreEmbarazo.visibility = View.VISIBLE
                }

                1 -> {
                    // Ocultar todos los campos de embarazo cuando se selecciona "No"
                    binding.layoutSiEmbarazo.visibility = View.GONE
                    binding.tfFechaUltimaMenstruacion.visibility = View.GONE
                    binding.tfSemanasGestacion.visibility = View.GONE
                    binding.tfPesoPreEmbarazo.visibility = View.GONE
                    // Limpiar los campos
                    binding.tfFechaUltimaMenstruacion.editText?.text = null
                    binding.tfSemanasGestacion.editText?.text = null
                    binding.tfPesoPreEmbarazo.editText?.text = null
                }
            }
        }
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

    private fun habilitarCampos() {
        // Mostrar botones de remover campo cuando es modo de edición
        mostrarBotonesRemoverCampo()

        // Ocultar mensajes de "sin datos" cuando se habilita la edición
        ocultarMensajesSinDatos()
    }

    private fun quitarErrores() {
        // Antropométricos
        binding.tfPeso.error = null
        binding.tfAltura.error = null
        binding.tfTalla.error = null
        binding.tfCircuferenciaBraquial.error = null
        binding.tfCircuferenciaCadera.error = null
        binding.tfCircuferenciaCintura.error = null
        binding.tfPerimetroCefalico.error = null
        binding.tfPliegueTricipital.error = null
        binding.tfPliegueSubescapular.error = null
        // Metabolico
        binding.tfGlicemiaBasal.error = null
        binding.tfGlicemiaPostprandial.error = null
        binding.tfGlicemiaAleatoria.error = null
        binding.tfHemoglobinaGlicosilada.error = null
        binding.tfTrigliceridos.error = null
        binding.tfColesterolTotal.error = null
        binding.tfColesterolHdl.error = null
        binding.tfColesterolLdl.error = null
        // Signos vitales
        binding.tfTensionArterialSistolica.error = null
        binding.tfTensionArterialDiastolica.error = null
        binding.tfFrecuenciaCardiaca.error = null
        binding.tfFrecuenciaRespiratoria.error = null
        binding.tfTemperatura.error = null
        binding.tfSTO2.error = null
        binding.tfPulso.error = null
        // Obstétrico
        binding.tfIsEmbarazo.error = null
        binding.tfFechaUltimaMenstruacion.error = null
        binding.tfSemanasGestacion.error = null
        binding.tfPesoPreEmbarazo.error = null
        // Pediatrico
        binding.tfUsaBiberon.error = null
        binding.tfTipoLactancia.error = null
    }

    private fun limpiarCampos() {
        quitarErrores()
        // Antropometrico
        binding.tfPeso.editText?.text = null
        binding.tfAltura.editText?.text = null
        binding.tfTalla.editText?.text = null
        binding.tfCircuferenciaBraquial.editText?.text = null
        binding.tfCircuferenciaCadera.editText?.text = null
        binding.tfCircuferenciaCintura.editText?.text = null
        binding.tfPerimetroCefalico.editText?.text = null
        binding.tfPliegueTricipital.editText?.text = null
        binding.tfPliegueSubescapular.editText?.text = null
        // Metabolico
        binding.tfGlicemiaBasal.editText?.text = null
        binding.tfGlicemiaPostprandial.editText?.text = null
        binding.tfGlicemiaAleatoria.editText?.text = null
        binding.tfHemoglobinaGlicosilada.editText?.text = null
        binding.tfTrigliceridos.editText?.text = null
        binding.tfColesterolTotal.editText?.text = null
        binding.tfColesterolHdl.editText?.text = null
        binding.tfColesterolLdl.editText?.text = null
        // Vitales
        binding.tfTensionArterialSistolica.editText?.text = null
        binding.tfTensionArterialDiastolica.editText?.text = null
        binding.tfFrecuenciaCardiaca.editText?.text = null
        binding.tfFrecuenciaRespiratoria.editText?.text = null
        binding.tfTemperatura.editText?.text = null
        binding.tfSTO2.editText?.text = null
        binding.tfPulso.editText?.text = null
        // Obstétrico
        binding.tfIsEmbarazo.editText?.text = null
        binding.tfFechaUltimaMenstruacion.editText?.text = null
        binding.tfSemanasGestacion.editText?.text = null
        binding.tfPesoPreEmbarazo.editText?.text = null
        // Pediatrico
        binding.tfUsaBiberon.editText?.text = null
        binding.tfTipoLactancia.editText?.text = null

    }

    private fun ocultarBotonesRemoverCampo() {
        binding.btnRemoverTensionArterialSistolica.visibility = View.GONE
        binding.btnRemoverTensionArterialDiastolica.visibility = View.GONE
        binding.btnRemoverFrecuenciaCardiaca.visibility = View.GONE
        binding.btnRemoverFrecuenciaRespiratoria.visibility = View.GONE
        binding.btnRemoverTemperatura.visibility = View.GONE
        binding.btnRemoverSTO2.visibility = View.GONE
        binding.btnRemoverPulso.visibility = View.GONE

        // Botones de remover datos metabólicos
        binding.btnRemoverGlicemiaBasal.visibility = View.GONE
        binding.btnRemoverGlicemiaPostprandial.visibility = View.GONE
        binding.btnRemoverGlicemiaAleatoria.visibility = View.GONE
        binding.btnRemoverHemoglobinaGlicosilada.visibility = View.GONE
        binding.btnRemoverTrigliceridos.visibility = View.GONE
        binding.btnRemoverColesterolTotal.visibility = View.GONE
        binding.btnRemoverColesterolHdl.visibility = View.GONE
        binding.btnRemoverColesterolLdl.visibility = View.GONE

        // Botones de remover datos antropométricos
        binding.btnRemoverPeso.visibility = View.GONE
        binding.btnRemoverAltura.visibility = View.GONE
        binding.btnRemoverTalla.visibility = View.GONE
        binding.btnRemoverCircuferenciaBraquial.visibility = View.GONE
        binding.btnRemoverCircuferenciaCadera.visibility = View.GONE
        binding.btnRemoverCircuferenciaCintura.visibility = View.GONE
        binding.btnRemoverPerimetroCefalico.visibility = View.GONE
        binding.btnRemoverPliegueTricipital.visibility = View.GONE
        binding.btnRemoverPliegueSubescapular.visibility = View.GONE
    }

    private fun verificarYMostrarMensajesSinDatos() {
        // Verificar signos vitales desde el ViewModel compartido
        val haySignosVitales: Boolean = sharedViewModel.detalleVital.value?.let { vital ->
            vital.tensionArterialSistolica != null ||
                    vital.tensionArterialDiastolica != null ||
                    vital.frecuenciaCardiaca != null ||
                    vital.frecuenciaRespiratoria != null ||
                    vital.temperatura != null ||
                    vital.saturacionOxigeno != null ||
                    vital.pulso != null
        } ?: false
        binding.tvSinDatosVitales.visibility = if (haySignosVitales) View.GONE else View.VISIBLE

        // Verificar datos metabólicos desde el ViewModel compartido
        val hayDatosMetabolicos: Boolean =
            sharedViewModel.detalleMetabolico.value?.let { metabolico ->
                metabolico.glicemiaBasal != null ||
                        metabolico.glicemiaPostprandial != null ||
                        metabolico.glicemiaAleatoria != null ||
                        metabolico.hemoglobinaGlicosilada != null ||
                        metabolico.trigliceridos != null ||
                        metabolico.colesterolTotal != null ||
                        metabolico.colesterolHdl != null ||
                        metabolico.colesterolLdl != null
            } ?: false
        binding.tvSinDatosMetabolicos.visibility =
            if (hayDatosMetabolicos) View.GONE else View.VISIBLE

        // Verificar datos antropométricos desde el ViewModel compartido
        val hayDatosAntropometricos: Boolean =
            sharedViewModel.detalleAntropometrico.value?.let { antropometrico ->
                antropometrico.peso != null ||
                        antropometrico.altura != null ||
                        antropometrico.talla != null ||
                        antropometrico.circunferenciaBraquial != null ||
                        antropometrico.circunferenciaCadera != null ||
                        antropometrico.circunferenciaCintura != null ||
                        antropometrico.perimetroCefalico != null ||
                        antropometrico.pliegueTricipital != null ||
                        antropometrico.pliegueSubescapular != null
            } ?: false
        binding.tvSinDatosAntropometricos.visibility =
            if (hayDatosAntropometricos) View.GONE else View.VISIBLE

        // Verificar datos obstétricos desde el ViewModel compartido
        val hayDatosObstetricos: Boolean =
            sharedViewModel.detalleObstetricia.value?.let { obstetricia ->
                obstetricia.estaEmbarazada != null ||
                        obstetricia.fechaUltimaMenstruacion != null ||
                        obstetricia.semanasGestacion != null ||
                        obstetricia.pesoPreEmbarazo != null
            } ?: false
        if (hayDatosObstetricos) {
            binding.tvSinDatosEmbarazo.visibility = View.GONE
            binding.tfIsEmbarazo.visibility = View.VISIBLE
        } else {
            binding.tvSinDatosEmbarazo.visibility = View.VISIBLE
            binding.tfIsEmbarazo.visibility = View.GONE
        }

        // Verificar datos pediátricos desde el ViewModel compartido
        val hayDatosPediatricos: Boolean =
            sharedViewModel.detallePediatrico.value?.let { pediatrico ->
                pediatrico.usaBiberon != null ||
                        pediatrico.tipoLactancia != null
            } ?: false
        binding.tvSinDatosPediatricos.visibility =
            if (hayDatosPediatricos) View.GONE else View.VISIBLE
    }

    private fun ocultarMensajesSinDatos() {
        binding.tvSinDatosVitales.visibility = View.GONE
        binding.tvSinDatosMetabolicos.visibility = View.GONE
        binding.tvSinDatosAntropometricos.visibility = View.GONE
        binding.tvSinDatosEmbarazo.visibility = View.GONE
        binding.tvSinDatosPediatricos.visibility = View.GONE
    }

    private fun mostrarBotonesRemoverCampo() {
        // Botones de remover signos vitales
        binding.btnRemoverTensionArterialSistolica.visibility = View.VISIBLE
        binding.btnRemoverTensionArterialDiastolica.visibility = View.VISIBLE
        binding.btnRemoverFrecuenciaCardiaca.visibility = View.VISIBLE
        binding.btnRemoverFrecuenciaRespiratoria.visibility = View.VISIBLE
        binding.btnRemoverTemperatura.visibility = View.VISIBLE
        binding.btnRemoverSTO2.visibility = View.VISIBLE
        binding.btnRemoverPulso.visibility = View.VISIBLE

        // Botones de remover datos metabólicos
        binding.btnRemoverGlicemiaBasal.visibility = View.VISIBLE
        binding.btnRemoverGlicemiaPostprandial.visibility = View.VISIBLE
        binding.btnRemoverGlicemiaAleatoria.visibility = View.VISIBLE
        binding.btnRemoverHemoglobinaGlicosilada.visibility = View.VISIBLE
        binding.btnRemoverTrigliceridos.visibility = View.VISIBLE
        binding.btnRemoverColesterolTotal.visibility = View.VISIBLE
        binding.btnRemoverColesterolHdl.visibility = View.VISIBLE
        binding.btnRemoverColesterolLdl.visibility = View.VISIBLE

        // Botones de remover datos antropométricos
        binding.btnRemoverPeso.visibility = View.VISIBLE
        binding.btnRemoverAltura.visibility = View.VISIBLE
        binding.btnRemoverTalla.visibility = View.VISIBLE
        binding.btnRemoverCircuferenciaBraquial.visibility = View.VISIBLE
        binding.btnRemoverCircuferenciaCadera.visibility = View.VISIBLE
        binding.btnRemoverCircuferenciaCintura.visibility = View.VISIBLE
        binding.btnRemoverPerimetroCefalico.visibility = View.VISIBLE
        binding.btnRemoverPliegueTricipital.visibility = View.VISIBLE
        binding.btnRemoverPliegueSubescapular.visibility = View.VISIBLE
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