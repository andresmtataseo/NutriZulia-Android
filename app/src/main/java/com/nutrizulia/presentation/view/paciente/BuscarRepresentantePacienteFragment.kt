package com.nutrizulia.presentation.view.paciente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.search.SearchView
import com.nutrizulia.databinding.FragmentBuscarRepresentantePacienteBinding
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.presentation.adapter.RepresentanteAdapter
import com.nutrizulia.presentation.viewmodel.paciente.BuscarRepresentantePacienteViewModel
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BuscarRepresentantePacienteFragment : Fragment() {

    private val viewModel: BuscarRepresentantePacienteViewModel by viewModels()
    private var _binding: FragmentBuscarRepresentantePacienteBinding? = null
    private val binding: FragmentBuscarRepresentantePacienteBinding get() = _binding!!
    private lateinit var representanteAdapter: RepresentanteAdapter
    private lateinit var representanteFiltradoAdapter: RepresentanteAdapter
    private var representanteSeleccionado: Representante? = null
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBuscarRepresentantePacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()
        setupRecyclerViews()
        setupListeners()
        setupObservers()

        parentFragmentManager.setFragmentResultListener(
            SeleccionarParentescoDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val parentescoId = bundle.getInt(SeleccionarParentescoDialogFragment.BUNDLE_KEY_PARENTESCO)

            representanteSeleccionado?.let { representante ->
                (parentFragment as? SeleccionarRepresentanteFragment)
                    ?.enviarResultadoSeleccion(representante.id, parentescoId)

                (parentFragment?.parentFragment as? BottomSheetDialogFragment)?.dismiss()
                    ?: (parentFragment as? BottomSheetDialogFragment)?.dismiss()
            }
        }

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
        representanteSeleccionado = representante
        showParentescoDialog()
    }

    // Dialog parentesco

    private fun showParentescoDialog() {
        val parentescoDialog = SeleccionarParentescoDialogFragment()
        parentescoDialog.show(parentFragmentManager, "ParentescoSelectionDialog")
    }

}