package com.nutrizulia.presentation.view.consulta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentConsultasBinding
import com.nutrizulia.presentation.adapter.PacienteConCitaAdapter
import com.nutrizulia.presentation.viewmodel.consulta.ConsultasViewModel
import com.nutrizulia.util.Utils
import com.nutrizulia.util.DateRangePickerUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConsultasFragment : Fragment() {

    private val viewModel: ConsultasViewModel by viewModels()
    private lateinit var binding: FragmentConsultasBinding
    private lateinit var pacienteConCitaAdapter: PacienteConCitaAdapter
    private lateinit var pacienteConCitaFiltradoAdapter: PacienteConCitaAdapter
    private lateinit var pacienteConCitaSearchAdapter: PacienteConCitaAdapter
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConsultasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()
        setupRecyclerViews()
        setupListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        // Reiniciar filtros al volver a la pantalla
        viewModel.limpiarFiltros()
        limpiarChipsSeleccionados()
        // Limpiar también el SearchView
        binding.searchView.getEditText().setText("")
        viewModel.obtenerConsultas()
    }

    private fun setupRecyclerViews() {
        pacienteConCitaAdapter = PacienteConCitaAdapter(
            emptyList(),
            onClickCardCitaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCardConsultaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesConsultaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCitaPerdidaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaPerdidaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }
        )

        pacienteConCitaFiltradoAdapter = PacienteConCitaAdapter(
            emptyList(),
            onClickCardCitaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCardConsultaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesConsultaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCitaPerdidaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaPerdidaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }
        )

        // Adapter para resultados de búsqueda dentro del SearchView
        pacienteConCitaSearchAdapter = PacienteConCitaAdapter(
            emptyList(),
            onClickCardCitaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCardConsultaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesConsultaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCitaPerdidaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaPerdidaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }
        )

        binding.recyclerViewConsultas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacienteConCitaAdapter
        }

        binding.recyclerViewCitasFiltradas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacienteConCitaFiltradoAdapter
        }

        // RecyclerView dentro del SearchView para resultados de búsqueda
        binding.recyclerViewConsultasFiltradas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacienteConCitaSearchAdapter
        }

    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.obtenerConsultas()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // Listener para el botón de mostrar/ocultar filtros
        binding.btnToggleFiltros.setOnClickListener {
            toggleFiltrosVisibility()
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWN) {
                if (binding.searchView.getEditText().text.isNullOrBlank()) {
                    viewModel.limpiarBusqueda()
                }
            }
            if (newState == SearchView.TransitionState.HIDDEN) {
                binding.searchView.getEditText().setText("")
                viewModel.limpiarBusqueda()
            }
        }

        binding.searchView.getEditText().addTextChangedListener { text ->
            val query: String = text?.toString()?.trim().orEmpty()
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(400)
                viewModel.buscarConsultas(query)
            }
        }

        // Listeners para chips de Estado
        binding.chipPendientes.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleEstadoFilter("PENDIENTE", isChecked)
        }
        binding.chipReprogramdas.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleEstadoFilter("REPROGRAMADA", isChecked)
        }
        binding.chipCompletadas.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleEstadoFilter("COMPLETADA", isChecked)
        }
        binding.chipCanceladas.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleEstadoFilter("CANCELADA", isChecked)
        }
        binding.chipNoAsistio.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleEstadoFilter("NO_ASISTIO", isChecked)
        }
        binding.chipSinPreviaCita.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleEstadoFilter("SIN_PREVIA_CITA", isChecked)
        }

        // Listeners para chips de Período
        binding.chipHoy.setOnCheckedChangeListener { _, isChecked ->
            viewModel.togglePeriodoFilter("hoy", isChecked)
        }
        binding.chipSemana.setOnCheckedChangeListener { _, isChecked ->
            viewModel.togglePeriodoFilter("esta semana", isChecked)
        }
        binding.chipMes.setOnCheckedChangeListener { _, isChecked ->
            viewModel.togglePeriodoFilter("este mes", isChecked)
        }
        binding.chipPersonalizado.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mostrarSelectorRangoFechas()
            } else {
                viewModel.clearCustomDateRange()
            }
        }

        // Listeners para chips de Tipo de Consulta
        binding.chipPrimeraVez.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTipoConsultaFilter("PRIMERA_CONSULTA", isChecked)
        }
        binding.chipSeguimiento.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTipoConsultaFilter("CONSULTA_SUCESIVA", isChecked)
        }

        // Listener para botón Limpiar filtros
        binding.btnLimpiarFiltros.setOnClickListener {
            viewModel.limpiarFiltros()
            limpiarChipsSeleccionados()
            // Limpiar también el texto del SearchView
            binding.searchView.getEditText().setText("")
        }

        binding.btnAgendar.apply {
            // Agrega los ítems del FAB expandible
            addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_agendar_cita, R.drawable.ic_cita)
                    .setLabel("Agendar cita")
                    .create()
            )
            addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_consulta_directa, R.drawable.ic_agregar_consulta)
                    .setLabel("Consulta sin cita")
                    .create()
            )

            // Listener para manejar las acciones seleccionadas
            setOnActionSelectedListener { actionItem ->
                when (actionItem.id) {
                    R.id.fab_agendar_cita -> {
                        findNavController().navigate(
                            ConsultasFragmentDirections.actionConsultasFragmentToSeleccionarPacienteCitaFragment(isAgendar = true)
                        )
                        false
                    }

                    R.id.fab_consulta_directa -> {
                        findNavController().navigate(
                            ConsultasFragmentDirections.actionConsultasFragmentToSeleccionarPacienteCitaFragment(isAgendar = false)
                        )
                        false
                    }

                    else -> false
                }
            }
        }


    }

    private fun setupObservers() {
        viewModel.pacientesConCitas.observe(viewLifecycleOwner) { consultas ->
            pacienteConCitaAdapter.updateCitas(consultas)
        }

        viewModel.pacientesConCitasFiltrados.observe(viewLifecycleOwner) { consultasFiltradas ->
            pacienteConCitaFiltradoAdapter.updateCitas(consultasFiltradas)
            // También actualizar los resultados del SearchView
            pacienteConCitaSearchAdapter.updateCitas(consultasFiltradas)
            // Mostrar RecyclerView filtrado si hay resultados de búsqueda o filtros
            updateRecyclerViewVisibility()
        }

        viewModel.filtrosActivos.observe(viewLifecycleOwner) { filtrosActivos ->
            updateRecyclerViewVisibility()
        }

        viewModel.filtro.observe(viewLifecycleOwner) { filtro ->
            updateRecyclerViewVisibility()
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Utils.mostrarSnackbar(binding.root, it)
                viewModel.clearMensaje()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.customDateRangeText.observe(viewLifecycleOwner) { rangeText ->
            if (rangeText != null) {
                binding.chipPersonalizado.text = rangeText
                binding.chipPersonalizado.isChecked = true
            } else {
                binding.chipPersonalizado.text = "Personalizado"
                binding.chipPersonalizado.isChecked = false
            }
        }
    }

    private fun updateRecyclerViewVisibility() {
        val filtrosActivos = viewModel.filtrosActivos.value ?: false
        val filtroTexto = viewModel.filtro.value?.isNotBlank() ?: false
        val hayConsultasFiltradas = viewModel.pacientesConCitasFiltrados.value?.isNotEmpty() ?: false
        
        // Mostrar RecyclerView filtrado si hay filtros activos, búsqueda de texto, o resultados filtrados
        if (filtrosActivos || filtroTexto || hayConsultasFiltradas) {
            binding.recyclerViewConsultas.visibility = View.GONE
            binding.recyclerViewCitasFiltradas.visibility = View.VISIBLE
        } else {
            binding.recyclerViewCitasFiltradas.visibility = View.GONE
            binding.recyclerViewConsultas.visibility = View.VISIBLE
        }
    }

    private fun toggleFiltrosVisibility() {
        val layoutFiltros = binding.layoutFiltros
        val btnToggle = binding.btnToggleFiltros
        
        if (layoutFiltros.visibility == View.GONE) {
            // Mostrar filtros
            layoutFiltros.visibility = View.VISIBLE
            btnToggle.text = "Ocultar filtros"
            btnToggle.setIconResource(R.drawable.ic_expand_less)
        } else {
            // Ocultar filtros
            layoutFiltros.visibility = View.GONE
            btnToggle.text = "Mostrar filtros"
            btnToggle.setIconResource(R.drawable.ic_expand_more)
        }
    }

    private fun limpiarChipsSeleccionados() {
        // Limpiar chips de Estado
        binding.chipPendientes.isChecked = false
        binding.chipCompletadas.isChecked = false
        binding.chipCanceladas.isChecked = false
        binding.chipReprogramdas.isChecked = false
        binding.chipNoAsistio.isChecked = false
        binding.chipSinPreviaCita.isChecked = false

        // Limpiar chips de Período
        binding.chipHoy.isChecked = false
        binding.chipSemana.isChecked = false
        binding.chipMes.isChecked = false
        binding.chipPersonalizado.isChecked = false
        binding.chipPersonalizado.text = "Personalizado" // Restablecer texto original

        // Limpiar chips de Tipo de Consulta
        binding.chipSeguimiento.isChecked = false
        binding.chipPrimeraVez.isChecked = false
    }

    private fun mostrarSelectorRangoFechas() {
        DateRangePickerUtil.showDateRangePicker(
            fragmentManager = parentFragmentManager,
            onDateRangeSelected = { startDate, endDate, displayText ->
                viewModel.setCustomDateRange(startDate, endDate, displayText)
            }
        )
    }

}