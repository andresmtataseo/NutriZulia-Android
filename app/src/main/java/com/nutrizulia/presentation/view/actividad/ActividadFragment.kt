package com.nutrizulia.presentation.view.actividad

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.databinding.FragmentActividadBinding
import com.nutrizulia.presentation.adapter.ActividadAdapter
import com.nutrizulia.presentation.viewmodel.actividad.ActividadViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActividadFragment : Fragment() {

    private lateinit var binding: FragmentActividadBinding
    private val viewModel: ActividadViewModel by viewModels()
    private lateinit var actividadAdapter: ActividadAdapter
    private lateinit var actividadFiltradoAdapter: ActividadAdapter
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActividadBinding.inflate(inflater, container, false)
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
        viewModel.obtenerActividades()
    }

    private fun setupRecyclerViews() {
        actividadAdapter = ActividadAdapter(
            emptyList(),
            onClickListener = { actividadConTipo -> onActividadConTipoClick(actividadConTipo) })
        actividadFiltradoAdapter = ActividadAdapter(
            emptyList(),
            onClickListener = { actividadConTipo -> onActividadConTipoClick(actividadConTipo) })

        binding.recyclerViewActividades.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = actividadAdapter
        }

        binding.recyclerViewActividadesFiltradas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = actividadFiltradoAdapter
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.obtenerActividades()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWN) {
                if (binding.searchView.getEditText().text.isNullOrBlank()) {
                    viewModel.buscarActividades("")
                }
            }
            if (newState == SearchView.TransitionState.HIDDEN) {
                viewModel.buscarActividades("")
            }
        }

        binding.searchView.getEditText().addTextChangedListener { text ->
            val query: String = text?.toString()?.trim().orEmpty()
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(400)
                viewModel.buscarActividades(query)
            }
        }

        binding.btnRegistrarActividad.setOnClickListener {
            findNavController().navigate(ActividadFragmentDirections.actionActividadFragmentToRegistrarActividadFragment(null, true))
        }
    }

    private fun setupObservers() {
        viewModel.actividades.observe(viewLifecycleOwner) { actividades ->
            actividadAdapter.updateActividades(actividades)
        }

        viewModel.actividadesFiltradas.observe(viewLifecycleOwner) { actividadesFiltradas ->
            actividadFiltradoAdapter.updateActividades(actividadesFiltradas)
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                mostrarSnackbar(binding.root, it)
                viewModel.clearMensaje()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun onActividadConTipoClick(actividadConTipo: ActividadConTipo) {
        findNavController().navigate(
            ActividadFragmentDirections.actionActividadFragmentToAccionesActividadFragment(
                actividadConTipo.actividadId
            )
        )
    }
}
