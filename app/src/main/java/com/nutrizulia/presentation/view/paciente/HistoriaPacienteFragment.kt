package com.nutrizulia.presentation.view.paciente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.nutrizulia.databinding.FragmentHistoriaPacienteBinding
import com.nutrizulia.presentation.viewmodel.paciente.HistoriaPacienteViewModel
import com.nutrizulia.util.Utils
import com.nutrizulia.presentation.adapter.PacienteConCitaAdapter
import com.nutrizulia.presentation.adapter.PacienteConConsulaYDetalleAdapter
import com.nutrizulia.presentation.view.consulta.ConsultasFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoriaPacienteFragment : Fragment() {

    private lateinit var binding: FragmentHistoriaPacienteBinding
    private val args: HistoriaPacienteFragmentArgs by navArgs()
    private val viewModel: HistoriaPacienteViewModel by viewModels()

    private lateinit var pacienteConCitaAdapter: PacienteConConsulaYDetalleAdapter
    private lateinit var pacienteConCitaFiltradoAdapter: PacienteConConsulaYDetalleAdapter
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoriaPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate(args.idPaciente)
        setupRecyclerViews()
        setupListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.obtenerConsultas(args.idPaciente)
    }

    private fun setupRecyclerViews() {
        pacienteConCitaAdapter = PacienteConConsulaYDetalleAdapter(
            emptyList(),
            onClickCardConsultaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesConsultaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }
        )

        pacienteConCitaFiltradoAdapter = PacienteConConsulaYDetalleAdapter(
            emptyList(),
            onClickCardConsultaListener = { pacienteConCita ->
                findNavController().navigate(
                    HistoriaPacienteFragmentDirections.actionHistoriaPacienteFragmentToAccionesConsultaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }
        )

        binding.rvHistoriaClinica.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacienteConCitaAdapter
        }

        binding.rvHistoriaClinicaFiltrada.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacienteConCitaFiltradoAdapter
        }

    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.obtenerConsultas(args.idPaciente)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWN) {
                if (binding.searchView.getEditText().text.isNullOrBlank()) {
                    viewModel.buscarConsultas(args.idPaciente, "")
                }
            }
            if (newState == SearchView.TransitionState.HIDDEN) {
                viewModel.buscarConsultas(args.idPaciente, "")
            }
        }

        binding.searchView.getEditText().addTextChangedListener { text ->
            val query: String = text?.toString()?.trim().orEmpty()
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(400)
                viewModel.buscarConsultas(args.idPaciente, query)
            }
        }

    }

    private fun setupObservers() {
        viewModel.consultasDetalladas.observe(viewLifecycleOwner) { it ->
            pacienteConCitaAdapter.updateCitas(it)
        }

        viewModel.pacientesConCitasFiltrados.observe(viewLifecycleOwner) { it ->
            pacienteConCitaFiltradoAdapter.updateCitas(it)
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
    }

}