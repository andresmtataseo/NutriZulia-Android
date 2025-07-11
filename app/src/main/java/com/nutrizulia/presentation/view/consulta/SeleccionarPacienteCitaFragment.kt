package com.nutrizulia.presentation.view.consulta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrizulia.databinding.FragmentSeleccionarPacienteCitaBinding
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.presentation.adapter.PacienteAdapter
import com.nutrizulia.presentation.viewmodel.SeleccionarPacienteCitaViewModel
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class SeleccionarPacienteCitaFragment : Fragment() {

    private val viewModel: SeleccionarPacienteCitaViewModel by viewModels()
    private val args: SeleccionarPacienteCitaFragmentArgs by navArgs()
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

        viewModel.eventoNavegacion.observe(viewLifecycleOwner) { event ->
            // Consume el evento para evitar la re-navegación
            event.getContentIfNotHandled()?.let { (pacienteId, consultaId) ->
                findNavController().navigate(
                    SeleccionarPacienteCitaFragmentDirections.actionSeleccionarPacienteCitaFragmentToRegistrarCitaFragment(pacienteId, consultaId)
                )
            }
        }

        viewModel.consultaProgramada.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { consulta ->
                val fecha = consulta.fechaHoraProgramada?.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val hora = consulta.fechaHoraProgramada?.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))

                Utils.mostrarDialog(
                    context = requireContext(),
                    title = "Reprogramar cita",
                    message = "Este paciente ya tiene una cita programada para el $fecha a las $hora. ¿Quieres reprogramarla?",
                    positiveButtonText = "Reprogramar",
                    onPositiveClick = {
                        findNavController().navigate(
                            SeleccionarPacienteCitaFragmentDirections.actionSeleccionarPacienteCitaFragmentToRegistrarCitaFragment(
                                consulta.pacienteId,
                                consulta.id
                            )
                        )
                    }
                )
            }
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Utils.mostrarSnackbar(binding.root, it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun onPacienteClick(paciente: Paciente) {
        if (args.isAgendar) {
            viewModel.verificarCita(paciente)
        } else {
            // Navegar directo a consulta sin cita
            findNavController().navigate(
                SeleccionarPacienteCitaFragmentDirections.actionSeleccionarPacienteCitaFragmentToRegistrarConsultaGraph2(
                    idConsulta = null,
                    isEditable = true,
                    idPaciente = paciente.id,
                )
            )
        }
    }

}