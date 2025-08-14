package com.nutrizulia.presentation.view.actividad

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.databinding.FragmentActividadBinding
import com.nutrizulia.presentation.adapter.ActividadAdapter
import com.nutrizulia.presentation.view.paciente.PacientesFragmentDirections
import com.nutrizulia.presentation.viewmodel.actividad.ActividadViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActividadFragment : Fragment() {

    private lateinit var binding: FragmentActividadBinding
    private val viewModel: ActividadViewModel by viewModels()
    private lateinit var actividadAdapter: ActividadAdapter
    private lateinit var actividadFiltradoAdapter: ActividadAdapter

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
        viewModel.obtenerRepresentantes()
    }

    private fun setupRecyclerViews() {
        actividadAdapter = ActividadAdapter(
            emptyList(),
            onClickListener = { actividadConTipo -> onActividadConTipoClick(actividadConTipo) })
        actividadFiltradoAdapter = ActividadAdapter(
            emptyList(),
            onClickListener = { actividadConTipo -> onActividadConTipoClick(actividadConTipo) })

        binding.recyclerViewPacientes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = actividadAdapter
        }

        binding.recyclerViewPacientesFiltrados.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = actividadFiltradoAdapter
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.obtenerRepresentantes()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.searchView.getEditText().addTextChangedListener { text ->
            val query = text.toString().trim()
            viewModel.buscarRepresentantes(query)
        }

        binding.btnRegistrarActividad.setOnClickListener {
//            findNavController().navigate(
//                PacientesFragmentDirections.actionPacientesFragmentToRegistrarPacienteFragment(
//                    null, true
//                )
//            )
        }
    }

    private fun setupObservers() {
        // Pacientes
        viewModel.actividades.observe(viewLifecycleOwner) { pacientes ->
            actividadAdapter.updateActividades(pacientes)
        }


        // Pacientes filtrados
        viewModel.actividadesFiltradas.observe(viewLifecycleOwner) { pacientesFiltrados ->
            actividadFiltradoAdapter.updateActividades(pacientesFiltrados)
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

    private fun onActividadConTipoClick(actividadConTipo: ActividadConTipo) {
//        findNavController().navigate(
//            ActividadFragmentDirections.actionPacientesFragmentToAccionesPacienteFragment(actividadConTipo.id)
//        )
    }
}
