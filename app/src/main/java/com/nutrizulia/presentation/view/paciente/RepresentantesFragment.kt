package com.nutrizulia.presentation.view.paciente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.nutrizulia.databinding.FragmentRepresentantesBinding
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.presentation.adapter.RepresentanteAdapter
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepresentantesFragment : BottomSheetDialogFragment() {

    private val viewModel: RepresentantesViewModel by viewModels()

    private var _binding: FragmentRepresentantesBinding? = null
    private val binding: FragmentRepresentantesBinding get() = _binding!!

    private lateinit var representanteAdapter: RepresentanteAdapter
    private lateinit var representanteFiltradoAdapter: RepresentanteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepresentantesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()
        setupRecyclerViews()
        setupListeners()
        setupObservers()
        setupTabLayout()
    }

    override fun onResume() {
        super.onResume()
        viewModel.obtenerPacientes()
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
            viewModel.obtenerPacientes()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.searchView.getEditText().addTextChangedListener { text ->
            val query = text.toString().trim()
            viewModel.buscarPacientes(query)
        }

        binding.btnRegistrar.setOnClickListener {

            mostrarSnackbar(binding.root, "Botón de registro presionado")
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
                mostrarSnackbar(binding.root, it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun onRepresentanteClick(representante: Representante) {
        mostrarSnackbar(binding.root, "Representante clickeado ${representante.nombres}")
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                handleTabSelection(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No es necesaria una acción aquí
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No es necesaria una acción aquí
            }
        })
    }

    private fun handleTabSelection(position: Int) {
        when (position) {
            0 -> { // Pestaña de Buscar
                binding.searchContent.isVisible = true
                binding.registerContent.isVisible = false
            }

            1 -> { // Pestaña de Registrar
                binding.searchContent.isVisible = false
                binding.registerContent.isVisible = true
            }
        }
    }
}