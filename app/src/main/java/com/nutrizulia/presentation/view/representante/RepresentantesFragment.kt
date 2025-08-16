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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.search.SearchView
import com.nutrizulia.databinding.FragmentRepresentantesBinding
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.presentation.adapter.RepresentanteAdapter
import com.nutrizulia.presentation.view.paciente.PacientesFragmentDirections
import com.nutrizulia.presentation.viewmodel.representante.RepresentantesViewModel
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepresentantesFragment : Fragment() {

    private val viewModel: RepresentantesViewModel by viewModels()

    private var _binding: FragmentRepresentantesBinding? = null
    private val binding: FragmentRepresentantesBinding get() = _binding!!

    private lateinit var representanteAdapter: RepresentanteAdapter
    private lateinit var representanteFiltradoAdapter: RepresentanteAdapter
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRepresentantesBinding.inflate(inflater, container, false)
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
        viewModel.obtenerRepresentantes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViews() {
        representanteAdapter = RepresentanteAdapter(
            emptyList(),
            onClickListener = { paciente -> onRepresentanteClick(paciente) })
        representanteFiltradoAdapter = RepresentanteAdapter(
            emptyList(),
            onClickListener = { paciente -> onRepresentanteClick(paciente) })

        binding.recyclerViewRepresentantes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = representanteAdapter
        }

        binding.recyclerViewRepresentantesFiltrados.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = representanteFiltradoAdapter
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.obtenerRepresentantes()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWN) {
                if (binding.searchView.getEditText().text.isNullOrBlank()) {
                    viewModel.buscarRepresentantes("")
                }
            }
            if (newState == SearchView.TransitionState.HIDDEN) {
                viewModel.buscarRepresentantes("")
            }
        }

        binding.searchView.getEditText().addTextChangedListener { text ->
            val query: String = text?.toString()?.trim().orEmpty()
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(400)
                viewModel.buscarRepresentantes(query)
            }
        }

        binding.btnRegistrarRepresentante.setOnClickListener {
            findNavController().navigate(
                RepresentantesFragmentDirections.actionRepresentantesFragmentToRegistrarRepresentanteFragment(
                    null, true
                )
            )
        }
    }

    private fun setupObservers() {
        viewModel.representantes.observe(viewLifecycleOwner) { pacientes ->
            representanteAdapter.updateRepresentantes(pacientes)
        }

        viewModel.representantesFiltrados.observe(viewLifecycleOwner) { pacientesFiltrados ->
            representanteFiltradoAdapter.updateRepresentantes(pacientesFiltrados)
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

    private fun onRepresentanteClick(representante: Representante) {
        findNavController().navigate(
            RepresentantesFragmentDirections.actionRepresentantesFragmentToAccionesRepresentanteFragment(
                representante.id
            )
        )
    }

}