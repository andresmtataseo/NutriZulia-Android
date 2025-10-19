package com.nutrizulia.presentation.view.consulta

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.TipoValorCalculado
import com.nutrizulia.databinding.FragmentRegistrarConsulta3Binding
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
import com.nutrizulia.domain.model.catalog.Enfermedad
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import com.nutrizulia.presentation.adapter.RiesgoBiologicoAdapter
import com.nutrizulia.presentation.viewmodel.consulta.ConsultaSharedViewModel
import com.nutrizulia.presentation.viewmodel.consulta.EvaluacionesFinalesViewModel
import com.nutrizulia.util.ModoConsulta
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class EvaluacionesFinalesFragment : Fragment() {

    private val sharedViewModel: ConsultaSharedViewModel by navGraphViewModels(R.id.registrarConsultaGraph) {
        defaultViewModelProviderFactory
    }
    private val viewModel: EvaluacionesFinalesViewModel by viewModels()

    private lateinit var binding: FragmentRegistrarConsulta3Binding
    private lateinit var diagnosticoAdapter: RiesgoBiologicoAdapter
    private val decimalFormat = DecimalFormat("#.##")
    private var dataLoadedForUI: Boolean = false
    private var isHistoria: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarConsulta3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtener el valor de isHistoria desde el SharedViewModel
        isHistoria = sharedViewModel.isHistoria.value ?: false
        val timestamp = System.currentTimeMillis()
        val consultaId = sharedViewModel.consulta.value?.id ?: ""
        Log.d(
            "NavFlow",
            "EvaluacionesFinalesFragment: onViewCreated con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=$consultaId"
        )

        // Observar cambios en isHistoria desde el SharedViewModel
        sharedViewModel.isHistoria.observe(viewLifecycleOwner) { historiaValue ->
            if (isHistoria != historiaValue) {
                isHistoria = historiaValue
                Log.d(
                    "NavFlow",
                    "EvaluacionesFinalesFragment: isHistoria actualizado desde SharedViewModel: $isHistoria"
                )
            }
        }

        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        setupSharedViewModelObservers()
        setupLocalViewModelObservers()
        setupDataLoadingObserver()
    }

    /**
     * Configura los observadores del SharedViewModel para estado global
     */
    private fun setupSharedViewModelObservers() {
        // Estado de carga
        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Mensajes globales
        sharedViewModel.mensaje.observe(viewLifecycleOwner) { message ->
            if (message.isNotBlank()) {
                Utils.mostrarSnackbar(binding.root, message)
            }
        }

        // Navegación de salida
        sharedViewModel.salir.observe(viewLifecycleOwner) { shouldExit ->
            if (shouldExit) {
                handleNavigation()
            }
        }

        // Modo de consulta (ver/editar)
        sharedViewModel.modoConsulta.observe(viewLifecycleOwner) { mode ->
            val isReadOnly = mode == ModoConsulta.VER_CONSULTA
            if (isReadOnly) {
                deshabilitarCampos()
            } else {
                habilitarCampos()
            }

            // Actualizar el estado del adaptador cuando cambie el modo de consulta
            if (::diagnosticoAdapter.isInitialized) {
                val isReadOnlyMode = isHistoria || mode == ModoConsulta.VER_CONSULTA
                diagnosticoAdapter.updateReadOnlyMode(isReadOnlyMode)
            }
        }

        // Evaluaciones antropométricas
        sharedViewModel.evaluacionesAntropometricas.observe(viewLifecycleOwner) { evaluaciones ->
            renderEvaluations(evaluaciones)
        }

        // Datos de la consulta
        sharedViewModel.consulta.observe(viewLifecycleOwner) { consulta ->
            consulta?.let {
                binding.tfObservaciones.editText?.setText(it.observaciones.orEmpty())
                binding.tfPlanes.editText?.setText(it.planes.orEmpty())
            }
        }
    }

    /**
     * Maneja la navegación de salida según el contexto
     */
    private fun handleNavigation() {
        val destinationId =
            if (isHistoria && sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                val pacienteId = sharedViewModel.paciente.value?.id
                if (pacienteId != null) R.id.historiaPacienteFragment else R.id.consultasFragment
            } else {
                R.id.consultasFragment
            }
        findNavController().popBackStack(destinationId, false)
    }

    /**
     * Configura los observadores del ViewModel local para diagnósticos y evaluaciones
     */
    private fun setupLocalViewModelObservers() {
        // Mensajes locales
        viewModel.mensaje.observe(viewLifecycleOwner) { message ->
            if (message.isNotBlank()) {
                Utils.mostrarSnackbar(binding.root, message)
            }
        }

        // Diagnósticos seleccionados (actuales)
        viewModel.diagnosticosSeleccionados.observe(viewLifecycleOwner) { diagnosticos ->
            diagnosticoAdapter.submitList(diagnosticos)
            updateSinDatosVisibility()
        }

        // Diagnósticos históricos
        viewModel.diagnosticosHistoricosRiesgo.observe(viewLifecycleOwner) { diagnosticosHistoricos ->
            // Historical diagnoses are now handled differently - they are part of the initial data
            updateSinDatosVisibility()
        }

        // Estado de primera consulta y visibilidad de información
        viewModel.esPrimeraConsulta.observe(viewLifecycleOwner) { esPrimera ->
            // Mostrar información sobre diagnósticos históricos solo si no es primera consulta
            binding.tvInfoDiagnosticos.visibility = if (esPrimera) View.GONE else View.VISIBLE
            // Note: First consultation state is now handled in the ViewModel
        }

        // Estado de diagnóstico principal
        viewModel.tieneDiagnosticoPrincipal.observe(viewLifecycleOwner) { tienePrincipal ->
            // Note: Principal diagnosis state is now handled in the ViewModel
        }

        // Evaluaciones calculadas localmente - se actualizan en el SharedViewModel
        viewModel.evaluacionesCalculadas.observe(viewLifecycleOwner) { evaluaciones ->
            sharedViewModel.updateEvaluacionesAntropometricas(evaluaciones)
        }
    }

    /**
     * Configura el observador para la carga inicial de datos
     */
    private fun setupDataLoadingObserver() {
        // Orquestación de carga de datos
        sharedViewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            if (paciente != null && !dataLoadedForUI) {
                val consulta =
                    sharedViewModel.consulta.value ?: sharedViewModel.consultaEditando.value
                val detalleAntro = sharedViewModel.detalleAntropometrico.value

                viewModel.loadInitialData(paciente, consulta?.id)

                if (detalleAntro != null && consulta != null && sharedViewModel.modoConsulta.value != ModoConsulta.VER_CONSULTA) {
                    Log.d("EvaluacionesFinalesFragment", "Cargando evaluación antropométrica")
                    viewModel.performAnthropometricEvaluation(paciente, detalleAntro, consulta)
                }

                dataLoadedForUI = true
            }
        }
    }

    private fun setupListeners() {
        binding.btnAgregarDiagnostico.setOnClickListener {
            // Validar si se puede agregar el diagnóstico
            if (!puedeAgregarDiagnostico()) {
                return@setOnClickListener
            }

            // Obtenemos los diagnósticos disponibles desde el viewModel local
            val diagnosticosDisponibles = viewModel.diagnosticosDisponibles.value
            if (!diagnosticosDisponibles.isNullOrEmpty()) {
                mostrarDialogoConDiagnosticos(diagnosticosDisponibles)
            } else {
                Utils.mostrarSnackbar(
                    binding.root,
                    "No hay diagnósticos disponibles para este paciente."
                )
            }
        }



        binding.btnRegistrarConsulta.setOnClickListener {
            if (sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                val timestamp = System.currentTimeMillis()
                val consultaId =
                    sharedViewModel.consulta.value?.id ?: sharedViewModel.consultaEditando.value?.id
                    ?: "desconocido"

                if (isHistoria) {
                    // Si viene de la historia del paciente, navegar de vuelta a HistoriaPacienteFragment
                    val pacienteId = sharedViewModel.paciente.value?.id
                    Log.d(
                        "NavFlow",
                        "EvaluacionesFinalesFragment: Saliendo hacia HistoriaPacienteFragment con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=$consultaId"
                    )
                    if (pacienteId != null) {
                        findNavController().popBackStack(R.id.historiaPacienteFragment, false)
                    } else {
                        findNavController().popBackStack(R.id.consultasFragment, false)
                    }
                } else {
                    Log.d(
                        "NavFlow",
                        "EvaluacionesFinalesFragment: Saliendo hacia ConsultasFragment con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=$consultaId"
                    )
                    findNavController().popBackStack(R.id.consultasFragment, false)
                }
                return@setOnClickListener
            }

            // 1. Obtener la lista de diagnósticos finales desde el viewModel local
            val consultaId =
                sharedViewModel.consulta.value?.id ?: sharedViewModel.consultaEditando.value?.id
                ?: return@setOnClickListener
            val diagnosticosFinales = viewModel.createDiagnosticosEntities(consultaId)

            // 2. Actualizar la lista de diagnósticos en el viewModel compartido
            sharedViewModel.updateDiagnosticos(diagnosticosFinales)

            // 3. Llamar a la función final para guardar todo desde el viewModel compartido
            sharedViewModel.saveCompleteConsultation(
                observaciones = binding.tfObservaciones.editText?.text.toString(),
                planes = binding.tfPlanes.editText?.text.toString()
            )
        }

        binding.btnLimpiar.setOnClickListener {
            Utils.mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Desea limpiar todos los campos?",
                "Sí", "No",
                { limpiarCampos() }, { },
                true
            )
        }
    }

    private fun renderEvaluations(evaluaciones: List<EvaluacionAntropometrica>) {
        // Lógica de pintado de UI sin cambios, ya que solo depende de la lista de evaluaciones
        binding.contentImcEdad.visibility = View.GONE
        binding.contentCircunferenciaCefalicaEdad.visibility = View.GONE
        binding.contentPesoAltura.visibility = View.GONE
        binding.contentPesoEdad.visibility = View.GONE
        binding.contentPesoTalla.visibility = View.GONE
        binding.contentTallaEdad.visibility = View.GONE
        binding.contentAlturaEdad.visibility = View.GONE
        binding.contentImc.visibility = View.GONE

        // Mostrar/ocultar el TextView de "sin datos antropométricos" según si hay evaluaciones
        if (evaluaciones.isEmpty()) {
            binding.tvSinDatosAntropometricos.visibility = View.VISIBLE
        } else {
            binding.tvSinDatosAntropometricos.visibility = View.GONE
        }

        for (evaluacion in evaluaciones) {
            val formattedValue = decimalFormat.format(evaluacion.valorCalculado)
            when (evaluacion.tipoIndicadorId) {
                1 -> { // IMC/Edad
                    binding.contentImcEdad.visibility = View.VISIBLE
                    if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                        binding.tiImcEdadZscore.setText(formattedValue)
                        binding.tiDiagnosticoAntropometricoImcEdad.setText(evaluacion.diagnosticoAntropometrico)
                    } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                        binding.tiImcEdadPercentil.setText(formattedValue)
                    }
                }

                2 -> { // Circunferencia Cefálica/Edad
                    binding.contentCircunferenciaCefalicaEdad.visibility = View.VISIBLE
                    if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                        binding.tiCircunferenciaCefalicaEdadZscore.setText(formattedValue)
                        binding.tiDiagnosticoAntropometricoCircunferenciaCefalicaEdad.setText(
                            evaluacion.diagnosticoAntropometrico
                        )
                    } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                        binding.tiCircunferenciaCefalicaEdadPercentil.setText(formattedValue)
                    }
                }
                // ... resto de los when cases (sin cambios)
                3 -> { // Peso/Altura
                    binding.contentPesoAltura.visibility = View.VISIBLE
                    if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                        binding.tiPesoAlturaZscore.setText(formattedValue)
                        binding.tiDiagnosticoAntropometricoPesoAltura.setText(evaluacion.diagnosticoAntropometrico)
                    } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                        binding.tiPesoAlturaPercentil.setText(formattedValue)
                    }
                }

                4 -> { // Peso/Edad
                    binding.contentPesoEdad.visibility = View.VISIBLE
                    if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                        binding.tiPesoEdadZscore.setText(formattedValue)
                        binding.tiDiagnosticoAntropometricoPesoEdad.setText(evaluacion.diagnosticoAntropometrico)
                    } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                        binding.tiPesoEdadPercentil.setText(formattedValue)
                    }
                }

                5 -> { // Peso/Talla
                    binding.contentPesoTalla.visibility = View.VISIBLE
                    if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                        binding.tiPesoTallaZscore.setText(formattedValue)
                        binding.tiDiagnosticoAntropometricoPesoTalla.setText(evaluacion.diagnosticoAntropometrico)
                    } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                        binding.tiPesoTallaPercentil.setText(formattedValue)
                    }
                }

                6 -> { // Talla/Edad
                    binding.contentTallaEdad.visibility = View.VISIBLE
                    if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                        binding.tiTallaEdadZscore.setText(formattedValue)
                        binding.tiDiagnosticoAntropometricoTallaEdad.setText(evaluacion.diagnosticoAntropometrico)
                    } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                        binding.tiTallaEdadPercentil.setText(formattedValue)
                    }
                }

                7 -> { // Altura/Edad
                    binding.contentAlturaEdad.visibility = View.VISIBLE
                    if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                        binding.tiAlturaEdadZscore.setText(formattedValue)
                        binding.tiDiagnosticoAntropometricoAlturaEdad.setText(evaluacion.diagnosticoAntropometrico)
                    } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                        binding.tiAlturaEdadPercentil.setText(formattedValue)
                    }
                }

                8 -> { // IMC Adulto
                    binding.contentImc.visibility = View.VISIBLE
                    binding.tiImc.setText(formattedValue)
                    binding.tiDiagnosticoAntropometricoImc.setText(evaluacion.diagnosticoAntropometrico)
                }
            }
        }
    }

    // --- Funciones de Ayuda (con cambios mínimos) ---

    private fun onDiagnosticoClick(diagnostico: com.nutrizulia.presentation.viewmodel.consulta.DiagnosticoParaUI) {
        val nombreDiagnostico = diagnostico.nombreCompleto
        Utils.mostrarDialog(
            requireContext(), "Eliminar Diagnóstico",
            "¿Desea eliminar el Diagnóstico $nombreDiagnostico?", "Sí", "No",
            { viewModel.eliminarDiagnostico(diagnostico) }, { }, true
        )
    }

    private fun setupRecyclerView() {
        // Determinar si está en modo de solo lectura basado en isHistoria o VER_CONSULTA
        val isReadOnlyMode =
            isHistoria || sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA

        diagnosticoAdapter = RiesgoBiologicoAdapter(
            onEliminarClick = { diagnostico -> onDiagnosticoClick(diagnostico) },
            isReadOnlyMode = isReadOnlyMode
        )

        binding.recyclerViewDiagnosticos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = diagnosticoAdapter
        }

        viewModel.diagnosticosSeleccionados.value?.let { diagnosticos ->
            diagnosticoAdapter.submitList(diagnosticos)
        }
    }

    private fun updateSinDatosVisibility() {
        val diagnosticosActuales = viewModel.diagnosticosSeleccionados.value ?: emptyList()
        val diagnosticosHistoricos = viewModel.diagnosticosHistoricosRiesgo.value ?: emptyList()
        val totalDiagnosticos = diagnosticosActuales.size + diagnosticosHistoricos.size

        binding.tvSinDatos.visibility = if (totalDiagnosticos == 0) View.VISIBLE else View.GONE
        binding.tvInfoDiagnosticos.visibility =
            if (diagnosticosHistoricos.isNotEmpty()) View.VISIBLE else View.GONE
    }

    /**
     * Valida si se puede agregar un diagnóstico según el contexto actual
     */
    private fun puedeAgregarDiagnostico(): Boolean {
        return when {
            sharedViewModel.paciente.value == null -> {
                Utils.mostrarSnackbar(binding.root, "No se ha seleccionado un paciente")
                false
            }

            sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA -> {
                Utils.mostrarSnackbar(
                    binding.root,
                    "No se pueden agregar diagnósticos en modo consulta"
                )
                false
            }

            else -> {
                val esPrimeraConsulta = viewModel.esPrimeraConsulta.value ?: false
                val tieneDiagnosticoPrincipal = viewModel.tieneDiagnosticoPrincipal.value ?: false
                val diagnosticosActuales = viewModel.diagnosticosSeleccionados.value ?: emptyList()

                if (esPrimeraConsulta && tieneDiagnosticoPrincipal && diagnosticosActuales.isNotEmpty()) {
                    Utils.mostrarSnackbar(
                        binding.root,
                        "En primera consulta solo se permite un diagnóstico principal."
                    )
                    false
                } else {
                    true
                }
            }
        }
    }

    /**
     * Habilita o deshabilita los campos de entrada según el modo de consulta
     */
    private fun habilitarCampos() {
        setCamposEnabled(true)
        binding.btnRegistrarConsulta.isEnabled = true
        binding.btnLimpiar.isEnabled = true
        binding.btnAgregarDiagnostico.visibility = View.VISIBLE
    }

    private fun deshabilitarCampos() {
        setCamposEnabled(false)
        binding.btnLimpiar.visibility = View.GONE
        binding.btnAgregarDiagnostico.visibility = View.GONE
        binding.btnRegistrarConsulta.setText("Salir")
        binding.btnRegistrarConsulta.icon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_atras)
    }

    private fun setCamposEnabled(enabled: Boolean) {
        binding.tfObservaciones.editText?.isEnabled = enabled
        binding.tfPlanes.editText?.isEnabled = enabled
    }

    private fun limpiarCampos() {
        binding.tfObservaciones.editText?.text = null
        binding.tfPlanes.editText?.text = null
    }

    private fun mostrarDialogoConDiagnosticos(diagnosticosDisponibles: List<RiesgoBiologico>) {
        val nombresDiagnosticos = diagnosticosDisponibles.map { it.nombre }.toTypedArray()
        val diagnosticosSeleccionados = mutableSetOf<Int>()

        val titulo = obtenerTituloDialogo()

        crearDialogoDiagnosticos(
            nombresDiagnosticos,
            titulo,
            diagnosticosSeleccionados
        ) { indices ->
            procesarDiagnosticosSeleccionados(diagnosticosDisponibles, indices)
        }
    }

    private fun obtenerTituloDialogo(): String {
        val esPrimeraConsulta = viewModel.esPrimeraConsulta.value ?: false
        val tieneDiagnosticoPrincipal = viewModel.tieneDiagnosticoPrincipal.value ?: false

        return when {
            esPrimeraConsulta && !tieneDiagnosticoPrincipal -> "Seleccionar Diagnóstico Principal"
            esPrimeraConsulta && tieneDiagnosticoPrincipal -> "Seleccionar Diagnósticos Adicionales"
            else -> "Seleccionar Diagnósticos Adicionales"
        }
    }

    private fun crearDialogoDiagnosticos(
        nombresDiagnosticos: Array<String>,
        titulo: String,
        diagnosticosSeleccionados: MutableSet<Int>,
        onConfirmar: (Set<Int>) -> Unit
    ) {
        val preseleccion = diagnosticosSeleccionados.firstOrNull() ?: -1
        var seleccionado = preseleccion
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(titulo)
            .setSingleChoiceItems(nombresDiagnosticos, preseleccion) { _, which ->
                seleccionado = which
            }
            .setPositiveButton("Agregar") { _, _ ->
                diagnosticosSeleccionados.clear()
                if (seleccionado >= 0) diagnosticosSeleccionados.add(seleccionado)
                onConfirmar(diagnosticosSeleccionados)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun procesarDiagnosticosSeleccionados(
        diagnosticosDisponibles: List<RiesgoBiologico>,
        indices: Set<Int>
    ) {
        indices.forEach { index ->
            val diagnosticoSeleccionado = diagnosticosDisponibles[index]

            if (esDiagnosticoOtros(diagnosticoSeleccionado)) {
                mostrarDialogoSeleccionEnfermedad(diagnosticoSeleccionado)
            } else {
                viewModel.agregarDiagnostico(diagnosticoSeleccionado)
            }
        }
    }

    private fun esDiagnosticoOtros(diagnostico: RiesgoBiologico): Boolean {
        return diagnostico.nombre.uppercase().contains("OTROS")
    }

    private fun mostrarDialogoSeleccionEnfermedad(diagnosticoOtros: RiesgoBiologico) {
        val dialogView = crearVistaDialogoEnfermedad()
        val autoCompleteTextView = dialogView.findViewWithTag<AutoCompleteTextView>("autocomplete")

        val enfermedadSeleccionada =
            configurarSeleccionEnfermedad(autoCompleteTextView, diagnosticoOtros)

        crearDialogoSeleccionEnfermedad(diagnosticoOtros, dialogView, enfermedadSeleccionada)
    }

    private fun configurarSeleccionEnfermedad(
        autoCompleteTextView: AutoCompleteTextView,
        riesgoBiologico: RiesgoBiologico
    ): Array<Enfermedad?> {
        val enfermedadSeleccionada = arrayOf<Enfermedad?>(null)
        val enfermedadesFiltradas = mutableListOf<Enfermedad>()

        // Cargar enfermedades iniciales
        viewModel.loadEnfermedades()

        // Configurar adaptador y observador con filtro por riesgo
        configurarAdaptadorEnfermedades(
            autoCompleteTextView,
            riesgoBiologico,
            enfermedadesFiltradas
        )
        configurarFiltroEnfermedades(autoCompleteTextView)
        configurarSeleccionItem(autoCompleteTextView, enfermedadesFiltradas, enfermedadSeleccionada)

        return enfermedadSeleccionada
    }

    private fun configurarAdaptadorEnfermedades(
        autoCompleteTextView: AutoCompleteTextView,
        riesgoBiologico: RiesgoBiologico,
        enfermedadesFiltradas: MutableList<Enfermedad>
    ) {
        viewModel.enfermedadesDisponibles.observe(viewLifecycleOwner) { enfermedades ->
            // Excluir enfermedades ya seleccionadas para el mismo riesgo
            val seleccionadosMismoRiesgo = viewModel.diagnosticosSeleccionados.value.orEmpty()
                .filter { it.riesgoBiologico.id == riesgoBiologico.id && it.enfermedad != null }
                .mapNotNull { it.enfermedad?.id }
                .toSet()
            val listaFiltrada = enfermedades.filter { it.id !in seleccionadosMismoRiesgo }

            enfermedadesFiltradas.clear()
            enfermedadesFiltradas.addAll(listaFiltrada)

            val nombresEnfermedades =
                listaFiltrada.map { "${it.nombre} (${it.codigoInternacional})" }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nombresEnfermedades
            )
            autoCompleteTextView.setAdapter(adapter)
        }
    }

    private fun configurarSeleccionItem(
        autoCompleteTextView: AutoCompleteTextView,
        enfermedadesFiltradas: MutableList<Enfermedad>,
        enfermedadSeleccionada: Array<Enfermedad?>
    ) {
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            if (position < enfermedadesFiltradas.size) {
                enfermedadSeleccionada[0] = enfermedadesFiltradas[position]
            }
        }
    }

    private fun crearDialogoSeleccionEnfermedad(
        diagnosticoOtros: RiesgoBiologico,
        dialogView: android.widget.LinearLayout,
        enfermedadSeleccionada: Array<Enfermedad?>
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Seleccionar Enfermedad Específica")
            .setMessage("Diagnóstico: ${diagnosticoOtros.nombre}")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                procesarSeleccionEnfermedad(diagnosticoOtros, enfermedadSeleccionada[0])
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun procesarSeleccionEnfermedad(
        diagnosticoOtros: RiesgoBiologico,
        enfermedad: Enfermedad?
    ) {
        enfermedad?.let {
            viewModel.agregarDiagnosticoConEnfermedad(diagnosticoOtros, it)
            Utils.mostrarSnackbar(
                binding.root,
                "Diagnóstico agregado: ${diagnosticoOtros.nombre} - ${it.nombre}"
            )
        } ?: run {
            Utils.mostrarSnackbar(binding.root, "Debe seleccionar una enfermedad específica")
        }
    }

    private fun crearVistaDialogoEnfermedad(): android.widget.LinearLayout {
        val linearLayout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val textInputLayout =
            com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
                hint = "Buscar enfermedad..."
                boxBackgroundMode =
                    com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
            }

        val autoCompleteTextView =
            com.google.android.material.textfield.MaterialAutoCompleteTextView(requireContext())
                .apply {
                    threshold = 1
                    val typedValue = android.util.TypedValue()
                    requireContext().theme.resolveAttribute(
                        com.google.android.material.R.attr.colorSurface,
                        typedValue,
                        true
                    )
                    val surfaceColor = if (typedValue.resourceId != 0) {
                        androidx.core.content.ContextCompat.getColor(
                            requireContext(),
                            typedValue.resourceId
                        )
                    } else {
                        typedValue.data
                    }
                    setDropDownBackgroundDrawable(
                        android.graphics.drawable.ColorDrawable(
                            surfaceColor
                        )
                    )
                    tag = "autocomplete"
                }

        textInputLayout.addView(autoCompleteTextView)
        linearLayout.addView(textInputLayout)

        return linearLayout
    }

    private fun configurarFiltroEnfermedades(autoCompleteTextView: android.widget.AutoCompleteTextView) {
        val textWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val filtro = s?.toString() ?: ""
                if (filtro.length >= 1) {
                    viewModel.loadEnfermedades(filtro)
                }
            }
        }
        autoCompleteTextView.addTextChangedListener(textWatcher)
    }
}