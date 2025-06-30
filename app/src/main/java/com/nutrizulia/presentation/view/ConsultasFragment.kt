package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentConsultasBinding
import com.nutrizulia.presentation.viewmodel.ConsultasViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConsultasFragment : Fragment() {

    private val viewModel: ConsultasViewModel by viewModels()
    private lateinit var binding: FragmentConsultasBinding
//    private lateinit var citaConPacienteAdapter: CitaConPacienteAdapter
//    private var listaOriginalCitasConPacientes: List<CitaConPaciente> = emptyList()

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

        binding.btnAgendar.setOnClickListener {
            findNavController().navigate(R.id.action_consultasFragment_to_seleccionarPacienteCitaFragment)
        }
//
//        viewModel.onCreate()
//
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            viewModel.obtenerCitas()
//        }
//
//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            binding.swipeRefreshLayout.isRefreshing = isLoading
//        }
//
//
//        viewModel.citasConPacientes.observe(viewLifecycleOwner) { citasConPacientes ->
//            if (!citasConPacientes.isNullOrEmpty()) {
//                listaOriginalCitasConPacientes = citasConPacientes
//                citaConPacienteAdapter = CitaConPacienteAdapter(
//                    citasConPacientes,
//                    onClickCardCitaListener = { citaConPaciente ->
//                        findNavController().navigate(ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaFragment(citaConPaciente.cita.id))
//                    },
//                    onClickCardConsultaListener = { citaConPaciente ->
//                        findNavController().navigate(ConsultasFragmentDirections.actionConsultasFragmentToAccionesConsultaFragment(citaConPaciente.cita.id))
//                    }, onClickCitaPerdidaListener = { citaConPaciente ->
//                        findNavController().navigate(ConsultasFragmentDirections.actionConsultasFragmentToAccionesCitaPerdidaFragment(citaConPaciente.cita.id))
//                    }
//                )
//
//                // Inicializar recyclerView principal
//                binding.recyclerViewConsultas.apply {
//                    layoutManager = LinearLayoutManager(requireContext())
//                    adapter = citaConPacienteAdapter
//                }
//
//                // Activar buscador ahora que el adapter existe
//                binding.searchView.getEditText().addTextChangedListener { text ->
//                    val textoFiltrado = text.toString().trim()
//                    val citasFiltradas = listaOriginalCitasConPacientes.filter { cita ->
//                        cita.cita.tipoCita.contains(textoFiltrado, ignoreCase = true) ||
//                                cita.cita.especialidad.contains(textoFiltrado, ignoreCase = true) ||
//                                cita.cita.estado.contains(textoFiltrado, ignoreCase = true)
//                    }
//
//                    citaConPacienteAdapter.updateCitas(citasFiltradas)
//                    binding.recyclerViewCitasFiltradas.apply {
//                        layoutManager = LinearLayoutManager(requireContext())
//                        adapter = citaConPacienteAdapter
//                    }
//                }
//            }
//        }
//
        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mostrarSnackbar(binding.root, mensaje)
        }

    }
    
}