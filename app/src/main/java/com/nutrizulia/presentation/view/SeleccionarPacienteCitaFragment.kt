package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentSeleccionarPacienteCitaBinding
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.presentation.adapter.PacienteAdapter
import com.nutrizulia.presentation.viewmodel.SeleccionarPacienteCitaViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeleccionarPacienteCitaFragment : Fragment() {

    private val viewModel: SeleccionarPacienteCitaViewModel by viewModels()
    private lateinit var binding: FragmentSeleccionarPacienteCitaBinding
    private lateinit var pacienteAdapter: PacienteAdapter
    private var listaOriginalPacientes: List<Paciente> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeleccionarPacienteCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onCreate()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.obtenerPacientes()
        }

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        })


        viewModel.pacientes.observe(viewLifecycleOwner, Observer { pacientes ->
            if (!pacientes.isNullOrEmpty()) {
                listaOriginalPacientes = pacientes
                pacienteAdapter = PacienteAdapter(
                    pacientes,
                    onClickListener = { paciente ->
                        findNavController().navigate(SeleccionarPacienteCitaFragmentDirections.actionSeleccionarPacienteCitaFragmentToRegistrarCitaFragment(paciente.id))})

                // Inicializar recyclerView principal
                binding.recyclerViewPacientes.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = pacienteAdapter
                }

                // Activar buscador ahora que el adapter existe
                binding.searchView.getEditText().addTextChangedListener { text ->
                    val textoFiltrado = text.toString().trim()
                    val pacientesFiltrados = listaOriginalPacientes.filter { paciente ->
                        paciente.primerNombre.contains(textoFiltrado, ignoreCase = true) ||
                                paciente.primerApellido.contains(
                                    textoFiltrado,
                                    ignoreCase = true
                                ) ||
                                paciente.segundoApellido.contains(
                                    textoFiltrado,
                                    ignoreCase = true
                                ) ||
                                paciente.cedula.contains(textoFiltrado, ignoreCase = true)
                    }

                    pacienteAdapter.updatePacientes(pacientesFiltrados)
                    binding.recyclerViewPacientesFiltrados.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = pacienteAdapter
                    }
                }
            }
        })

        viewModel.mensaje.observe(viewLifecycleOwner, Observer { mensaje ->
            mostrarSnackbar(binding.root, mensaje)
        })

        binding.btnRegistrarPaciente.setOnClickListener {
            findNavController().navigate(R.id.action_seleccionarPacienteCitaFragment_to_registrarPacienteFragment)
        }

    }

    private fun onPacienteClick(paciente: Paciente) {
        mostrarSnackbar(binding.root, "Paciente ${paciente.primerNombre} seleccionado")
    }

}