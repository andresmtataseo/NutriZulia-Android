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
import com.nutrizulia.presentation.viewmodel.ConsultasViewModel
import com.nutrizulia.databinding.FragmentConsultasBinding
import com.nutrizulia.domain.model.Cita
import com.nutrizulia.presentation.adapter.CitaAdapter
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConsultasFragment : Fragment() {

    private val viewModel: ConsultasViewModel by viewModels()
    private lateinit var binding: FragmentConsultasBinding
    private lateinit var citaAdapter: CitaAdapter
    private var listaOriginalCitas: List<Cita> = emptyList()

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

        viewModel.onCreate()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.obtenerCitas()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }


        viewModel.citas.observe(viewLifecycleOwner) { citas ->
            if (!citas.isNullOrEmpty()) {
                listaOriginalCitas = citas
                citaAdapter = CitaAdapter(citas, onClickListener = { cita -> onCitaClick(cita) })

                // Inicializar recyclerView principal
                binding.recyclerViewConsultas.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = citaAdapter
                }

                // Activar buscador ahora que el adapter existe
                binding.searchView.getEditText().addTextChangedListener { text ->
                    val textoFiltrado = text.toString().trim()
                    val citasFiltradas = listaOriginalCitas.filter { cita ->
                        cita.tipoCita.contains(textoFiltrado, ignoreCase = true) ||
                                cita.especialidad.contains(textoFiltrado, ignoreCase = true) ||
                                cita.estado.contains(textoFiltrado, ignoreCase = true)
                    }

                    citaAdapter.updateCitas(citasFiltradas)
                    binding.recyclerViewCitasFiltradas.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = citaAdapter
                    }
                }
            }
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mostrarSnackbar(binding.root, mensaje)
        }

    }

    private fun onCitaClick(cita: Cita) {
        findNavController().navigate(R.id.action_consultasFragment_to_registrarConsultaFragment)
    }
}