package com.nutrizulia.presentation.view.consulta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nutrizulia.R
import com.nutrizulia.data.local.enum.TipoValorCalculado
import com.nutrizulia.databinding.FragmentRegistrarConsulta3Binding
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import com.nutrizulia.presentation.adapter.RiesgoBiologicoAdapter
import com.nutrizulia.presentation.viewmodel.RegistrarConsultaViewModel
import com.nutrizulia.util.ModoConsulta
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import kotlin.collections.component1
import kotlin.collections.component2

@AndroidEntryPoint
class RegistrarConsulta3Fragment : Fragment() {

    private val viewModel: RegistrarConsultaViewModel by navGraphViewModels(R.id.registrarConsultaGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var binding: FragmentRegistrarConsulta3Binding
    private lateinit var riesgoBiologicoAdapter: RiesgoBiologicoAdapter
    private val decimalFormat = DecimalFormat("#.##")

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
        setupRecyclerView()
        setupListeners()
        setupObservers()
        viewModel.cargarDatosDiagnostico()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
            binding.content.visibility = if (it) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) {
            Utils.mostrarSnackbar(binding.root, it)
        }

        viewModel.modoConsulta.observe(viewLifecycleOwner) { modo ->
            if (modo == ModoConsulta.VER_CONSULTA) {
                deshabilitarCampos()
                riesgoBiologicoAdapter.setReadOnly(true)
            } else {
                habilitarCampos()
                riesgoBiologicoAdapter.setReadOnly(false)
            }
        }

        viewModel.consulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta != null) {
                binding.tfObservaciones.editText?.setText(consulta.observaciones.orEmpty())
                binding.tfPlanes.editText?.setText(consulta.planes.orEmpty())
            }
        }

        viewModel.riesgosBiologicosSeleccionados.observe(viewLifecycleOwner) { riesgos ->
            riesgoBiologicoAdapter.updateRiesgosBiologicos(riesgos)
            binding.tvSinDatos.visibility = if (riesgos.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.evaluacionesAntropometricas.observe(viewLifecycleOwner) { evaluaciones ->
            Log.d("RegistrarConsulta3Fragment", "Evaluaciones: $evaluaciones")
            Log.d("RegistrarConsulta3Fragment", "Evaluaciones: ${evaluaciones.size}")

            // Ocultar todas las vistas para un estado limpio
            binding.contentImcEdad.visibility = View.GONE
            binding.contentCircunferenciaCefalicaEdad.visibility = View.GONE
            binding.contentPesoAltura.visibility = View.GONE
            binding.contentPesoEdad.visibility = View.GONE
            binding.contentPesoTalla.visibility = View.GONE
            binding.contentTallaEdad.visibility = View.GONE
            binding.contentAlturaEdad.visibility = View.GONE
            binding.contentImc.visibility = View.GONE

            // Iterar sobre cada evaluación individual para pintarla en su lugar
            for (evaluacion in evaluaciones) {
                val formattedValue = decimalFormat.format(evaluacion.valorCalculado)

                when (evaluacion.tipoIndicadorId) {
                    1 -> { // IMC/Edad
                        binding.contentImcEdad.visibility = View.VISIBLE
                        if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                            binding.tiImcEdadZscore.setText(formattedValue)
                        } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                            binding.tiImcEdadPercentil.setText(formattedValue)
                        }
                    }
                    2 -> { // Circunferencia Cefálica/Edad
                        binding.contentCircunferenciaCefalicaEdad.visibility = View.VISIBLE
                        if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                            binding.tiCircunferenciaCefalicaEdadZscore.setText(formattedValue)
                        } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                            binding.tiCircunferenciaCefalicaEdadPercentil.setText(formattedValue)
                        }
                    }
                    3 -> { // Peso/Altura
                        binding.contentPesoAltura.visibility = View.VISIBLE
                        if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                            binding.tiPesoAlturaZscore.setText(formattedValue)
                        } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                            binding.tiPesoAlturaPercentil.setText(formattedValue)
                        }
                    }
                    4 -> { // Peso/Edad
                        binding.contentPesoEdad.visibility = View.VISIBLE
                        if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                            binding.tiPesoEdadZscore.setText(formattedValue)
                        } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                            binding.tiPesoEdadPercentil.setText(formattedValue)
                        }
                    }
                    5 -> { // Peso/Talla
                        binding.contentPesoTalla.visibility = View.VISIBLE
                        if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                            binding.tiPesoTallaZscore.setText(formattedValue)
                        } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                            binding.tiPesoTallaPercentil.setText(formattedValue)
                        }
                    }
                    6 -> { // Talla/Edad
                        binding.contentTallaEdad.visibility = View.VISIBLE
                        if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                            binding.tiTallaEdadZscore.setText(formattedValue)
                        } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                            binding.tiTallaEdadPercentil.setText(formattedValue)
                        }
                    }
                    7 -> { // Altura/Edad
                        binding.contentAlturaEdad.visibility = View.VISIBLE
                        if (evaluacion.tipoValorCalculado == TipoValorCalculado.Z_SCORE) {
                            binding.tiAlturaEdadZscore.setText(formattedValue)
                        } else if (evaluacion.tipoValorCalculado == TipoValorCalculado.PERCENTIL) {
                            binding.tiAlturaEdadPercentil.setText(formattedValue)
                        }
                    }
                    8 -> { // IMC Adulto
                        binding.contentImc.visibility = View.VISIBLE
                        // Para IMC, no hay distinción Z-Score/Percentil
                        binding.tiImc.setText(formattedValue)
                    }
                }
            }
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack(R.id.consultasFragment, false)
        }
    }

    // Pintamos la UI basado en el tipo de indicador
//            evaluaciones.groupBy { it.tipoIndicadorId }.forEach { (indicatorId, evals) ->
//                val zScoreEval = evals.find { it.tipoValorCalculado == TipoValorCalculado.Z_SCORE }
//                val percentileEval = evals.find { it.tipoValorCalculado == TipoValorCalculado.PERCENTIL }
//                val imcEval = evals.find { it.tipoValorCalculado == TipoValorCalculado.IMC }
//
//                when (indicatorId) {
//                    1 -> { // IMC/Edad
//                        binding.contentImcEdad.visibility = View.VISIBLE
//                        binding.tiImcEdadZscore.setText(zScoreEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        binding.tiImcEdadPercentil.setText(percentileEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        // binding.tiImcEdadDiagnostico.setText(zScoreEval?.diagnosticoAntropometrico ?: "")
//                    }
//                    2 -> { // Circunferencia Cefálica/Edad
//                        binding.contentCircunferenciaCefalicaEdad.visibility = View.VISIBLE
//                        binding.tiCircunferenciaCefalicaEdadZscore.setText(zScoreEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        binding.tiCircunferenciaCefalicaEdadPercentil.setText(percentileEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        // binding.tiCircunferenciaCefalicaEdadDiagnostico.setText(zScoreEval?.diagnosticoAntropometrico ?: "")
//                    }
//                    3 -> { // Peso/Altura
//                        binding.contentPesoAltura.visibility = View.VISIBLE
//                        binding.tiPesoAlturaZscore.setText(zScoreEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        binding.tiPesoAlturaPercentil.setText(percentileEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        // binding.tiPesoAlturaDiagnostico.setText(zScoreEval?.diagnosticoAntropometrico ?: "")
//                    }
//                    4 -> { // Peso/Edad
//                        binding.contentPesoEdad.visibility = View.VISIBLE
//                        binding.tiPesoEdadZscore.setText(zScoreEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        binding.tiPesoEdadPercentil.setText(percentileEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        // binding.tiPesoEdadDiagnostico.setText(zScoreEval?.diagnosticoAntropometrico ?: "")
//                    }
//                    5 -> { // Peso/Talla
//                        binding.contentPesoTalla.visibility = View.VISIBLE
//                        binding.tiPesoTallaZscore.setText(zScoreEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        binding.tiPesoTallaPercentil.setText(percentileEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        // binding.tiPesoTallaDiagnostico.setText(zScoreEval?.diagnosticoAntropometrico ?: "")
//                    }
//                    6 -> { // Talla/Edad
//                        binding.contentTallaEdad.visibility = View.VISIBLE
//                        binding.tiTallaEdadZscore.setText(zScoreEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        binding.tiTallaEdadPercentil.setText(percentileEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        // binding.tiTallaEdadDiagnostico.setText(zScoreEval?.diagnosticoAntropometrico ?: "")
//                    }
//                    7 -> { // Altura/Edad
//                        binding.contentAlturaEdad.visibility = View.VISIBLE
//                        binding.tiAlturaEdadZscore.setText(zScoreEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        binding.tiAlturaEdadPercentil.setText(percentileEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        // binding.tiAlturaEdadDiagnostico.setText(zScoreEval?.diagnosticoAntropometrico ?: "")
//                    }
//                    8 -> { // IMC Adulto
//                        binding.contentImc.visibility = View.VISIBLE
//                        binding.tiImc.setText(imcEval?.valorCalculado?.let { decimalFormat.format(it) } ?: "")
//                        // binding.tiImcDiagnostico.setText(imcEval?.diagnosticoAntropometrico ?: "")
//                    }
//                }
//            }

    private fun setupListeners() {
        binding.btnAgregarRiesgoBiologico.setOnClickListener {
            val riesgosDisponibles = viewModel.riesgosBiologicosDisponibles.value
            if (!riesgosDisponibles.isNullOrEmpty()) {
                mostrarDialogoConRiesgos(riesgosDisponibles)
            } else {
                Utils.mostrarSnackbar(binding.root, "No hay riesgos biológicos disponibles para este paciente.")
            }
        }

        binding.btnRegistrarConsulta.setOnClickListener {
            if (viewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                findNavController().popBackStack(R.id.consultasFragment, false)
                return@setOnClickListener
            }

            viewModel.guardarConsultaCompleta(
                observaciones = binding.tfObservaciones.editText?.text.toString(),
                planes = binding.tfPlanes.editText?.text.toString()
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
    }

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

    private fun onRiesgoBiologicoClick(riesgoBiologico: RiesgoBiologico) {
        Utils.mostrarDialog(
            requireContext(),
            "Eliminar Riesgo Biológico",
            "¿Desea eliminar el Riesgo Biológico ${riesgoBiologico.nombre}?",
            "Sí",
            "No",
            { viewModel.eliminarRiesgoBiologico(riesgoBiologico) },
            { },
            true
        )
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