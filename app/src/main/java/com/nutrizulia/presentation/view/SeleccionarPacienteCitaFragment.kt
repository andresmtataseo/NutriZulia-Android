package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentSeleccionarPacienteCitaBinding
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.presentation.adapter.PacienteAdapter
import com.nutrizulia.presentation.viewmodel.SeleccionarPacienteCitaViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeleccionarPacienteCitaFragment : Fragment() {

    private val viewModel: SeleccionarPacienteCitaViewModel by viewModels()
    private lateinit var binding: FragmentSeleccionarPacienteCitaBinding
    private lateinit var pacienteAdapter: PacienteAdapter
    private lateinit var pacienteFiltradoAdapter: PacienteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSeleccionarPacienteCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerViews() {
        pacienteAdapter = PacienteAdapter(
            emptyList(),
            onClickListener = { paciente -> onPacienteClick(paciente) })
        pacienteFiltradoAdapter = PacienteAdapter(
            emptyList(),
            onClickListener = { paciente -> onPacienteClick(paciente) })

        binding.recyclerViewPacientes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacienteAdapter
        }

        binding.recyclerViewPacientesFiltrados.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacienteFiltradoAdapter
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Reseteamos el filtro para traer todos los pacientes de nuevo
            viewModel.buscarPacientes("")
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.searchView.getEditText().addTextChangedListener { text ->
            val query = text.toString().trim()
            viewModel.buscarPacientes(query)
        }

//        binding.btnRegistrarPaciente.setOnClickListener {
//            findNavController().navigate(R.id.action_pacientesFragment_to_registrarPacienteFragment)
//        }
    }

    private fun setupObservers() {
        // Pacientes
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pacientes.collectLatest { pacientes ->
                pacienteAdapter.updatePacientes(pacientes)
            }
        }

        // Pacientes filtrados
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pacientesFiltrados.collectLatest { pacientesFiltrados ->
                pacienteFiltradoAdapter.updatePacientes(pacientesFiltrados)
            }
        }

        // Mensajes
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mensaje.collectLatest { mensaje ->
                mensaje?.let {
                    mostrarSnackbar(binding.root, it)
                }
            }
        }

        // Loading
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.swipeRefreshLayout.isRefreshing = isLoading
            }
        }
    }

    private fun onPacienteClick(paciente: Paciente) {
        findNavController().navigate(
            SeleccionarPacienteCitaFragmentDirections.actionSeleccionarPacienteCitaFragmentToRegistrarCitaFragment(paciente.id)
        )
    }
}