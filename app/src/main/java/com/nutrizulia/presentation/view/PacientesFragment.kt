package com.nutrizulia.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentPacientesBinding
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.presentation.adapter.PacienteAdapter
import com.nutrizulia.presentation.viewmodel.PacientesViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PacientesFragment : Fragment() {

    private val viewModel: PacientesViewModel by viewModels()
    private lateinit var binding: FragmentPacientesBinding
    private lateinit var pacienteAdapter: PacienteAdapter
    private lateinit var pacienteFiltradoAdapter: PacienteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPacientesBinding.inflate(inflater, container, false)
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
        viewModel.obtenerPacientes()
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
            viewModel.obtenerPacientes()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.searchView.getEditText().addTextChangedListener { text ->
            val query = text.toString().trim()
            viewModel.buscarPacientes(query)
        }

        binding.btnRegistrarPaciente.setOnClickListener {
            findNavController().navigate(R.id.action_pacientesFragment_to_registrarPacienteFragment)
        }
    }

    private fun setupObservers() {
        // Pacientes
        viewModel.pacientes.observe(viewLifecycleOwner) { pacientes ->
            pacienteAdapter.updatePacientes(pacientes)
        }


        // Pacientes filtrados
        viewModel.pacientesFiltrados.observe(viewLifecycleOwner) { pacientesFiltrados ->
            pacienteFiltradoAdapter.updatePacientes(pacientesFiltrados)
        }

        // Mensajes
        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                mostrarSnackbar(binding.root, it)
            }
        }

        // Loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun onPacienteClick(paciente: Paciente) {
        findNavController().navigate(
            PacientesFragmentDirections.actionPacientesFragmentToAccionesPacienteFragment(paciente.id)
        )
    }
}
