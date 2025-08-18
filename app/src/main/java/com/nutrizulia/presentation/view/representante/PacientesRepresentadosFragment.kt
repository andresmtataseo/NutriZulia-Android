package com.nutrizulia.presentation.view.representante

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.nutrizulia.databinding.FragmentPacientesRepresentadosBinding
import com.nutrizulia.domain.model.collection.PacienteRepresentado
import com.nutrizulia.presentation.adapter.PacientesRepresentadosAdapter
import com.nutrizulia.presentation.viewmodel.representante.PacientesRepresentadosViewModel
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PacientesRepresentadosFragment : Fragment() {

    private val viewModel: PacientesRepresentadosViewModel by viewModels()

    private var _binding: FragmentPacientesRepresentadosBinding? = null
    private val binding: FragmentPacientesRepresentadosBinding get() = _binding!!
    private val args: PacientesRepresentadosFragmentArgs by navArgs()

    private lateinit var pacientesRepresentadosAdapter: PacientesRepresentadosAdapter
    private lateinit var pacientesRepresentadosFiltradosAdapter: PacientesRepresentadosAdapter
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPacientesRepresentadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()
        viewModel.setRepresentanteId(args.representanteId)
        setupRecyclerViews()
        setupListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.obtenerPacientesRepresentados()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViews() {
        pacientesRepresentadosAdapter = PacientesRepresentadosAdapter(
            emptyList(),
            onItemClick = { pacienteRepresentado -> onPacienteRepresentadoClick(pacienteRepresentado) }
        )
        pacientesRepresentadosFiltradosAdapter = PacientesRepresentadosAdapter(
            emptyList(),
            onItemClick = { pacienteRepresentado -> onPacienteRepresentadoClick(pacienteRepresentado) }
        )

        binding.recyclerViewPacientesRepresentados.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacientesRepresentadosAdapter
        }

        binding.recyclerViewPacientesRepresentadosFiltrados.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacientesRepresentadosFiltradosAdapter
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.obtenerPacientesRepresentados()
        }

        binding.searchView.editText.addTextChangedListener { text ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(300)
                val query = text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.buscarPacientesRepresentados(query)
                    binding.recyclerViewPacientesRepresentados.isVisible = false
                    binding.recyclerViewPacientesRepresentadosFiltrados.isVisible = true
                } else {
                    binding.recyclerViewPacientesRepresentados.isVisible = true
                    binding.recyclerViewPacientesRepresentadosFiltrados.isVisible = false
                }
            }
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                binding.recyclerViewPacientesRepresentados.isVisible = true
                binding.recyclerViewPacientesRepresentadosFiltrados.isVisible = false
            }
        }
    }

    private fun setupObservers() {
        viewModel.pacientesRepresentados.observe(viewLifecycleOwner) { pacientes ->
            pacientesRepresentadosAdapter.updatePacientesRepresentados(pacientes)
        }

        viewModel.pacientesRepresentadosFiltrados.observe(viewLifecycleOwner) { pacientesFiltrados ->
            pacientesRepresentadosFiltradosAdapter.updatePacientesRepresentados(pacientesFiltrados)
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

    private fun onPacienteRepresentadoClick(pacienteRepresentado: PacienteRepresentado) {
         findNavController().navigate(
             PacientesRepresentadosFragmentDirections.actionPacientesRepresentadosFragmentToAccionesPacienteFragment(
                 pacienteRepresentado.pacienteId
             )
         )
    }
}