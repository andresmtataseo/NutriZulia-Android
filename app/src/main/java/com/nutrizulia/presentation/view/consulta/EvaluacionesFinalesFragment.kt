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
    private lateinit var riesgoBiologicoAdapter: RiesgoBiologicoAdapter
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
        // --- Observadores de Estado Global (desde SharedViewModel) ---
        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading: Boolean ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        sharedViewModel.mensaje.observe(viewLifecycleOwner) { message: String ->
            if (message.isNotBlank()) {
                Utils.mostrarSnackbar(binding.root, message)
            }
        }

        sharedViewModel.salir.observe(viewLifecycleOwner) { shouldExit: Boolean ->
            if (shouldExit) {
                if (isHistoria && sharedViewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                    // Si venimos de la historia del paciente, volvemos a ella
                    val pacienteId = sharedViewModel.paciente.value?.id
                    if (pacienteId != null) {
                        findNavController().popBackStack(R.id.historiaPacienteFragment, false)
                    } else {
                        findNavController().popBackStack(R.id.consultasFragment, false)
                    }
                } else {
                    findNavController().popBackStack(R.id.consultasFragment, false)
                }
            }
        }

        sharedViewModel.modoConsulta.observe(viewLifecycleOwner) { mode: ModoConsulta ->
            if (mode == ModoConsulta.VER_CONSULTA) {
                deshabilitarCampos()
                riesgoBiologicoAdapter.setReadOnly(true)
            } else {
                habilitarCampos()
                riesgoBiologicoAdapter.setReadOnly(false)
            }
        }

        // --- Orquestación de Carga de Datos ---
        // Observamos los datos del sharedViewModel para iniciar la lógica del viewModel local.
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

        // --- Observadores de Lógica Local (desde EvaluacionesFinalesViewModel) ---
        viewModel.riesgosBiologicosSeleccionados.observe(viewLifecycleOwner) { riesgos ->
            riesgoBiologicoAdapter.updateRiesgosBiologicos(riesgos)
            binding.tvSinDatos.visibility = if (riesgos.isEmpty()) View.VISIBLE else View.GONE
        }

        // Cuando las evaluaciones se calculan, las actualizamos en el sharedViewModel
        viewModel.evaluacionesCalculadas.observe(viewLifecycleOwner) { evaluaciones ->
            sharedViewModel.updateEvaluacionesAntropometricas(evaluaciones)
        }

        // --- Observadores de UI (leen desde SharedViewModel que ahora tiene los datos actualizados) ---
        sharedViewModel.evaluacionesAntropometricas.observe(viewLifecycleOwner) { evaluaciones ->
            renderEvaluations(evaluaciones)
        }

        sharedViewModel.consulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta != null) {
                binding.tfObservaciones.editText?.setText(consulta.observaciones.orEmpty())
                binding.tfPlanes.editText?.setText(consulta.planes.orEmpty())
            }
        }
    }

    private fun setupListeners() {
        binding.btnAgregarRiesgoBiologico.setOnClickListener {
            // Obtenemos los riesgos disponibles desde el viewModel local
            val riesgosDisponibles = viewModel.riesgosBiologicosDisponibles.value
            if (!riesgosDisponibles.isNullOrEmpty()) {
                mostrarDialogoConRiesgos(riesgosDisponibles)
            } else {
                Utils.mostrarSnackbar(binding.root, "No hay riesgos biológicos disponibles para este paciente.")
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

    private fun onRiesgoBiologicoClick(riesgoBiologico: RiesgoBiologico) {
        Utils.mostrarDialog(requireContext(), "Eliminar Riesgo Biológico",
            "¿Desea eliminar el Riesgo Biológico ${riesgoBiologico.nombre}?", "Sí", "No",
            // Llamamos al método del viewModel local
            { viewModel.eliminarRiesgoBiologico(riesgoBiologico) }, { }, true
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
        riesgoBiologicoAdapter = RiesgoBiologicoAdapter(
            emptyList(),
            onClickListener = { riesgoBiologico -> onRiesgoBiologicoClick(riesgoBiologico) })

        binding.recyclerViewRiesgosBiologicos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = riesgoBiologicoAdapter
        }

        viewModel.riesgosBiologicosSeleccionados.value?.let { riesgos ->
            riesgoBiologicoAdapter.updateRiesgosBiologicos(riesgos)
        }
    }

    private fun habilitarCampos() {
        binding.btnRegistrarConsulta.isEnabled = true
        binding.btnLimpiar.isEnabled = true
        binding.btnAgregarRiesgoBiologico.visibility = View.VISIBLE
    }

    private fun deshabilitarCampos() {
        binding.tfObservaciones.editText?.isEnabled = false
        binding.tfPlanes.editText?.isEnabled = false
        binding.btnLimpiar.visibility = View.GONE
        binding.btnAgregarRiesgoBiologico.visibility = View.GONE
        binding.btnRegistrarConsulta.setText("Salir")
    }

    private fun limpiarCampos() {
        binding.tfObservaciones.editText?.text = null
        binding.tfPlanes.editText?.text = null
    }

    private fun mostrarDialogoConRiesgos(riesgosDisponibles: List<RiesgoBiologico>) {
        val nombresRiesgos = riesgosDisponibles.map { it.nombre }.toTypedArray()
        val riesgosSeleccionados = mutableSetOf<Int>()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Seleccionar Riesgos Biológicos")
            .setMultiChoiceItems(
                nombresRiesgos,
                null
            ) { _, which, isChecked ->
                if (isChecked) {
                    riesgosSeleccionados.add(which)
                } else {
                    riesgosSeleccionados.remove(which)
                }
            }
            .setPositiveButton("Agregar") { _, _ ->
                riesgosSeleccionados.forEach { index ->
                    val riesgoSeleccionado = riesgosDisponibles[index]
                    viewModel.agregarRiesgoBiologico(riesgoSeleccionado)
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}