package com.nutrizulia.presentation.view.consulta

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentRegistrarConsulta3Binding
import com.nutrizulia.domain.model.catalog.RiesgoBiologico
import com.nutrizulia.presentation.adapter.PacienteAdapter
import com.nutrizulia.presentation.adapter.RiesgoBiologicoAdapter
import com.nutrizulia.presentation.viewmodel.RegistrarConsultaViewModel
import com.nutrizulia.util.ModoConsulta
import com.nutrizulia.util.Utils
import com.nutrizulia.util.Utils.mostrarDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class RegistrarConsulta3Fragment : Fragment() {

    private val viewModel: RegistrarConsultaViewModel by navGraphViewModels(R.id.registrarConsultaGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var binding: FragmentRegistrarConsulta3Binding
    private lateinit var riesgoBiologicoAdapter: RiesgoBiologicoAdapter

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
        setupObservers()
        setupListeners()
        setupRecyclerView()
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
            Log.d("ModoConsulta", "Modo de consulta: $modo")
            when (modo) {
                ModoConsulta.CREAR_SIN_CITA,
                ModoConsulta.CULMINAR_CITA,
                ModoConsulta.EDITAR_CONSULTA -> {
                    habilitarCampos()
                    riesgoBiologicoAdapter.setReadOnly(false)
                    viewModel.realizarEvaluacionAntropometrica()
                }

                ModoConsulta.VER_CONSULTA -> {
                    deshabilitarCampos()
                    cargarRiesgosExistentes()
                    riesgoBiologicoAdapter.setReadOnly(true)
                }
            }
        }

        viewModel.consulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta != null) {
                binding.tfObservaciones.editText?.setText(consulta.observaciones.orEmpty())
                binding.tfPlanes.editText?.setText(consulta.planes.orEmpty())
                
                // Si es modo editar, cargar riesgos existentes
                if (viewModel.modoConsulta.value == ModoConsulta.EDITAR_CONSULTA) {
                    cargarRiesgosExistentes()
                }
            }
        }

        viewModel.riesgosBiologicosSeleccionados.observe(viewLifecycleOwner) { riesgos ->
            riesgoBiologicoAdapter.updateRiesgosBiologicos(riesgos)
            binding.tvSinDatos.visibility = if (riesgos.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.resultadoImcEdad.observe(viewLifecycleOwner) { resultado ->
            binding.contentImcEdad.visibility = View.VISIBLE
            binding.tiImcEdadZscore.setText(resultado?.zScore.toString())
            binding.tiImcEdadPercentil.setText(resultado?.percentil.toString())
//            binding.tiImcEdadDiagnostico.setText(resultado.diagnostico)
        }

        viewModel.resultadoCircunferenciaCefalicaEdad.observe(viewLifecycleOwner) { resultado ->
            binding.contentCircunferenciaCefalicaEdad.visibility = View.VISIBLE
            binding.tiCircunferenciaCefalicaEdadZscore.setText(resultado?.zScore.toString())
            binding.tiCircunferenciaCefalicaEdadPercentil.setText(resultado?.percentil.toString())
//            binding.tiCircunferenciaCefalicaEdadDiagnostico.setText(resultado.diagnostico)
        }

        viewModel.resultadoPesoAltura.observe(viewLifecycleOwner) { resultado ->
            binding.contentPesoAltura.visibility = View.VISIBLE
            binding.tiPesoAlturaZscore.setText(resultado?.zScore.toString())
            binding.tiPesoAlturaPercentil.setText(resultado?.percentil.toString())
//            binding.tiPesoAlturaDiagnostico.setText(resultado.diagnostico)
        }

        viewModel.resultadoPesoEdad.observe(viewLifecycleOwner) { resultado ->
            binding.contentPesoEdad.visibility = View.VISIBLE
            binding.tiPesoEdadZscore.setText(resultado?.zScore.toString())
            binding.tiPesoEdadPercentil.setText(resultado?.percentil.toString())
//            binding.tiPesoEdadDiagnostico.setText(resultado.diagnostico)
        }

        viewModel.resultadoPesoTalla.observe(viewLifecycleOwner) { resultado ->
            binding.contentPesoTalla.visibility = View.VISIBLE
            binding.tiPesoTallaZscore.setText(resultado?.zScore.toString())
            binding.tiPesoTallaPercentil.setText(resultado?.percentil.toString())
//            binding.tiPesoTallaDiagnostico.setText(resultado.diagnostico)
        }

        viewModel.resultadoTallaEdad.observe(viewLifecycleOwner) { resultado ->
            binding.contentTallaEdad.visibility = View.VISIBLE
            binding.tiTallaEdadZscore.setText(resultado?.zScore.toString())
            binding.tiTallaEdadPercentil.setText(resultado?.percentil.toString())
//            binding.tiTallaEdadDiagnostico.setText(resultado.diagnostico)
        }

        viewModel.resultadoAlturaEdad.observe(viewLifecycleOwner) { resultado ->
            binding.contentAlturaEdad.visibility = View.VISIBLE
            binding.tiAlturaEdadZscore.setText(resultado?.zScore.toString())
            binding.tiAlturaEdadPercentil.setText(resultado?.percentil.toString())
//            binding.tiAlturaEdadDiagnostico.setText(resultado.diagnostico)
        }

        viewModel.resultadoImc.observe(viewLifecycleOwner) { resultado ->
            binding.contentImc.visibility = View.VISIBLE
            binding.tiImcZscore.setText(resultado.zScore.toString())
            binding.tiImcPercentil.setText(resultado.percentil.toString())
//            binding.tiImcDiagnostico.setText(resultado.diagnostico)
        }


        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack(R.id.consultasFragment, false)
        }

    }

    private fun setupListeners() {

        binding.btnAgregarRiesgoBiologico.setOnClickListener {
            mostrarDialogoRiesgosBiologicos()
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

    private fun setupRecyclerView() {
        riesgoBiologicoAdapter = RiesgoBiologicoAdapter(
            emptyList(),
            onClickListener = { riesgoBiologico -> onRiesgoBiologicoClick(riesgoBiologico) })

        binding.recyclerViewRiesgosBiologicos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = riesgoBiologicoAdapter
        }

        // Inicializar con datos del ViewModel si están disponibles
        viewModel.riesgosBiologicosSeleccionados.value?.let { riesgos ->
            riesgoBiologicoAdapter.updateRiesgosBiologicos(riesgos)
        }
    }

    private fun onRiesgoBiologicoClick(riesgoBiologico: RiesgoBiologico) {
        mostrarDialog(
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
        binding.btnRegistrarConsulta.text = "Salir"
    }

    private fun quitarErrores() {

    }

    private fun limpiarCampos() {
        quitarErrores()
        binding.tfObservaciones.editText?.text = null
        binding.tfPlanes.editText?.text = null
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
            val adapter =
                ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, names)
            setAdapter(adapter)
            if (text.toString() !in names) {
                setText("", false)
            }
        }

        setOnItemClickListener { _, _, position, _ ->
            onItemSelected(currentItems[position])
        }
    }

    private fun mostrarDialogoRiesgosBiologicos() {
        // Verificar si ya hay riesgos disponibles cargados
        val riesgosActuales = viewModel.riesgosBiologicosDisponibles.value
        if (riesgosActuales != null) {
            mostrarDialogoConRiesgos(riesgosActuales)
        } else {
            // Solo cargar si no están disponibles
            viewModel.cargarRiesgosBiologicosDisponibles()
            
            // Observar una sola vez
            viewModel.riesgosBiologicosDisponibles.observe(viewLifecycleOwner) { riesgosDisponibles ->
                if (riesgosDisponibles.isNotEmpty()) {
                    mostrarDialogoConRiesgos(riesgosDisponibles)
                } else {
                    Utils.mostrarSnackbar(binding.root, "No hay riesgos biológicos disponibles para este paciente")
                }
            }
        }
    }

    private fun mostrarDialogoConRiesgos(riesgosDisponibles: List<RiesgoBiologico>) {
        val nombresRiesgos = riesgosDisponibles.map { it.nombre }.toTypedArray()
        val riesgosSeleccionados = mutableSetOf<Int>()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Seleccionar Riesgos Biológicos")
            .setMultiChoiceItems(
                nombresRiesgos,
                null
            ) { dialog, which, isChecked ->
                if (isChecked) {
                    riesgosSeleccionados.add(which)
                } else {
                    riesgosSeleccionados.remove(which)
                }
            }
            .setPositiveButton("Agregar") { dialog, which ->
                riesgosSeleccionados.forEach { index ->
                    val riesgoSeleccionado = riesgosDisponibles[index]
                    viewModel.agregarRiesgoBiologico(riesgoSeleccionado)
                }
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun cargarRiesgosExistentes() {
        viewModel.cargarRiesgosBiologicosExistentes()
    }

}
