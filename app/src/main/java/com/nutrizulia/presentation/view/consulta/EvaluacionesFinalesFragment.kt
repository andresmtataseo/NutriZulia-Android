package com.nutrizulia.presentation.view.consulta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.TipoValorCalculado
import com.nutrizulia.databinding.FragmentRegistrarConsulta3Binding
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrarConsulta3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtener el valor de isHistoria desde el SharedViewModel
        isHistoria = sharedViewModel.isHistoria.value ?: false
        val timestamp = System.currentTimeMillis()
        val consultaId = sharedViewModel.consulta.value?.id ?: ""
        Log.d("NavFlow", "EvaluacionesFinalesFragment: onViewCreated con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=$consultaId")
        
        // Observar cambios en isHistoria desde el SharedViewModel
        sharedViewModel.isHistoria.observe(viewLifecycleOwner) { historiaValue ->
            if (isHistoria != historiaValue) {
                isHistoria = historiaValue
                Log.d("NavFlow", "EvaluacionesFinalesFragment: isHistoria actualizado desde SharedViewModel: $isHistoria")
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
            diagnosticoAdapter.setReadOnly(isReadOnly)
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
        val destinationId = if (isHistoria && sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
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
            diagnosticoAdapter.updateRiesgosBiologicos(diagnosticos)
            updateSinDatosVisibility()
        }
        
        // Diagnósticos históricos
        viewModel.diagnosticosHistoricosRiesgo.observe(viewLifecycleOwner) { diagnosticosHistoricos ->
            diagnosticoAdapter.updateDiagnosticosHistoricos(diagnosticosHistoricos)
            updateSinDatosVisibility()
        }

        // Estado de primera consulta y visibilidad de información
        viewModel.esPrimeraConsulta.observe(viewLifecycleOwner) { esPrimera ->
            // Mostrar información sobre diagnósticos históricos solo si no es primera consulta
            binding.tvInfoDiagnosticos.visibility = if (esPrimera) View.GONE else View.VISIBLE
            diagnosticoAdapter.setEsPrimeraConsulta(esPrimera)
        }

        // Estado de diagnóstico principal
        viewModel.tieneDiagnosticoPrincipal.observe(viewLifecycleOwner) { tienePrincipal ->
            diagnosticoAdapter.setTieneDiagnosticoPrincipal(tienePrincipal)
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
                val consulta = sharedViewModel.consulta.value ?: sharedViewModel.consultaEditando.value
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
                Utils.mostrarSnackbar(binding.root, "No hay diagnósticos disponibles para este paciente.")
            }
        }



        binding.btnRegistrarConsulta.setOnClickListener {
            if (sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                val timestamp = System.currentTimeMillis()
                val consultaId = sharedViewModel.consulta.value?.id ?: sharedViewModel.consultaEditando.value?.id ?: "desconocido"
                
                if (isHistoria) {
                    // Si viene de la historia del paciente, navegar de vuelta a HistoriaPacienteFragment
                    val pacienteId = sharedViewModel.paciente.value?.id
                    Log.d("NavFlow", "EvaluacionesFinalesFragment: Saliendo hacia HistoriaPacienteFragment con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=$consultaId")
                    if (pacienteId != null) {
                        findNavController().popBackStack(R.id.historiaPacienteFragment, false)
                    } else {
                        findNavController().popBackStack(R.id.consultasFragment, false)
                    }
                } else {
                    Log.d("NavFlow", "EvaluacionesFinalesFragment: Saliendo hacia ConsultasFragment con isHistoria=$isHistoria | timestamp=$timestamp | consultaId=$consultaId")
                    findNavController().popBackStack(R.id.consultasFragment, false)
                }
                return@setOnClickListener
            }

            // 1. Obtener la lista de diagnósticos finales desde el viewModel local
            val consultaId = sharedViewModel.consulta.value?.id ?: sharedViewModel.consultaEditando.value?.id ?: return@setOnClickListener
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

    private fun renderEvaluations(evaluaciones: List<com.nutrizulia.domain.model.collection.EvaluacionAntropometrica>) {
        // Lógica de pintado de UI sin cambios, ya que solo depende de la lista de evaluaciones
        binding.contentImcEdad.visibility = View.GONE
        binding.contentCircunferenciaCefalicaEdad.visibility = View.GONE
        binding.contentPesoAltura.visibility = View.GONE
        binding.contentPesoEdad.visibility = View.GONE
        binding.contentPesoTalla.visibility = View.GONE
        binding.contentTallaEdad.visibility = View.GONE
        binding.contentAlturaEdad.visibility = View.GONE
        binding.contentImc.visibility = View.GONE

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
                        binding.tiDiagnosticoAntropometricoCircunferenciaCefalicaEdad.setText(evaluacion.diagnosticoAntropometrico)
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

    private fun onDiagnosticoClick(diagnostico: RiesgoBiologico) {
        Utils.mostrarDialog(requireContext(), "Eliminar Diagnóstico",
            "¿Desea eliminar el Diagnóstico ${diagnostico.nombre}?", "Sí", "No",
            // Llamamos al método del viewModel local
            { viewModel.eliminarDiagnostico(diagnostico) }, { }, true
        )
    }

//    private fun mostrarDialogoConRiesgos(riesgosDisponibles: List<RiesgoBiologico>) {
//        val nombresRiesgos = riesgosDisponibles.map { it.nombre }.toTypedArray()
//        val riesgosSeleccionados = mutableSetOf<Int>()
//        MaterialAlertDialogBuilder(requireContext()).setTitle("Seleccionar Riesgos Biológicos")
//            .setMultiChoiceItems(nombresRiesgos, null) { _, which, isChecked ->
//                if (isChecked) riesgosSeleccionados.add(which) else riesgosSeleccionados.remove(which)
//            }
//            .setPositiveButton("Agregar") { _, _ ->
//                riesgosSeleccionados.forEach { index ->
//                    // Llamamos al método del viewModel local
//                    viewModel.agregarRiesgoBiologico(riesgosDisponibles[index])
//                }
//            }
//            .setNegativeButton("Cancelar", null).show()
//    }

    private fun setupRecyclerView() {
        diagnosticoAdapter = RiesgoBiologicoAdapter(
            emptyList(),
            onClickListener = { diagnostico -> onDiagnosticoClick(diagnostico) })

        binding.recyclerViewDiagnosticos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = diagnosticoAdapter
        }

        viewModel.diagnosticosSeleccionados.value?.let { diagnosticos ->
            diagnosticoAdapter.updateRiesgosBiologicos(diagnosticos)
        }
    }
    
    private fun updateSinDatosVisibility() {
        val diagnosticosActuales = viewModel.diagnosticosSeleccionados.value ?: emptyList()
        val diagnosticosHistoricos = viewModel.diagnosticosHistoricosRiesgo.value ?: emptyList()
        val totalDiagnosticos = diagnosticosActuales.size + diagnosticosHistoricos.size
        
        binding.tvSinDatos.visibility = if (totalDiagnosticos == 0) View.VISIBLE else View.GONE
        binding.tvInfoDiagnosticos.visibility = if (diagnosticosHistoricos.isNotEmpty()) View.VISIBLE else View.GONE
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
                Utils.mostrarSnackbar(binding.root, "No se pueden agregar diagnósticos en modo consulta")
                false
            }
            else -> {
                val esPrimeraConsulta = viewModel.esPrimeraConsulta.value ?: false
                val tieneDiagnosticoPrincipal = viewModel.tieneDiagnosticoPrincipal.value ?: false
                val diagnosticosActuales = viewModel.diagnosticosSeleccionados.value ?: emptyList()
                
                if (esPrimeraConsulta && tieneDiagnosticoPrincipal && diagnosticosActuales.isNotEmpty()) {
                    Utils.mostrarSnackbar(binding.root, "En primera consulta solo se permite un diagnóstico principal.")
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
        
        val esPrimeraConsulta = viewModel.esPrimeraConsulta.value ?: false
        val tieneDiagnosticoPrincipal = viewModel.tieneDiagnosticoPrincipal.value ?: false
        
        val titulo = when {
            esPrimeraConsulta && !tieneDiagnosticoPrincipal -> "Seleccionar Diagnóstico Principal"
            esPrimeraConsulta && tieneDiagnosticoPrincipal -> "Seleccionar Diagnósticos Adicionales"
            else -> "Seleccionar Diagnósticos Adicionales"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(titulo)
            .setMultiChoiceItems(
                nombresDiagnosticos,
                null
            ) { _, which, isChecked ->
                if (isChecked) {
                    diagnosticosSeleccionados.add(which)
                } else {
                    diagnosticosSeleccionados.remove(which)
                }
            }
            .setPositiveButton("Agregar") { _, _ ->
                diagnosticosSeleccionados.forEach { index ->
                    val diagnosticoSeleccionado = diagnosticosDisponibles[index]
                    viewModel.agregarDiagnostico(diagnosticoSeleccionado)
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}