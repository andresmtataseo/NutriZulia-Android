package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentConsultasBinding
import com.nutrizulia.presentation.adapter.PacienteConCitaAdapter
import com.nutrizulia.presentation.viewmodel.ConsultasViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConsultasFragment : Fragment() {

    private val viewModel: ConsultasViewModel by viewModels()
    private lateinit var binding: FragmentConsultasBinding
    private lateinit var pacienteConCitaAdapter: PacienteConCitaAdapter
    private lateinit var pacienteConCitaFiltradoAdapter: PacienteConCitaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConsultasBinding.inflate(inflater, container, false)
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
        viewModel.obtenerConsultas()
    }

    private fun setupRecyclerViews() {
        pacienteConCitaAdapter = PacienteConCitaAdapter(
            emptyList(),
            onClickCardCitaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCardConsultaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesConsultaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCitaPerdidaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaPerdidaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }
        )

        pacienteConCitaFiltradoAdapter = PacienteConCitaAdapter(
            emptyList(),
            onClickCardCitaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCardConsultaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesConsultaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }, onClickCitaPerdidaListener = { pacienteConCita ->
                findNavController().navigate(
                    ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaPerdidaFragment(
                        pacienteConCita.consultaId
                    )
                )
            }
        )

        binding.recyclerViewConsultas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacienteConCitaAdapter
        }

        binding.recyclerViewCitasFiltradas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pacienteConCitaFiltradoAdapter
        }

    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.obtenerConsultas()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.searchView.getEditText().addTextChangedListener { text ->
            val query = text.toString().trim()
            viewModel.buscarConsultas(query)
        }

        binding.btnAgendar.setOnClickListener {
            findNavController().navigate(R.id.action_consultasFragment_to_seleccionarPacienteCitaFragment)
        }
    }

    private fun setupObservers() {
        viewModel.pacientesConCitas.observe(viewLifecycleOwner) { it ->
            pacienteConCitaAdapter.updateCitas(it)
        }

        viewModel.pacientesConCitasFiltrados.observe(viewLifecycleOwner) { it ->
            pacienteConCitaFiltradoAdapter.updateCitas(it)
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

}