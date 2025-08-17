package com.nutrizulia.presentation.view.paciente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrizulia.databinding.FragmentHistoriaPacienteBinding
import com.nutrizulia.presentation.adapter.EventoHistoriaClinicaAdapter
import com.nutrizulia.presentation.viewmodel.paciente.HistoriaPacienteViewModel
import com.nutrizulia.util.Utils.calcularEdad
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.google.android.material.snackbar.Snackbar
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoriaPacienteFragment : Fragment() {

    private val viewModel: HistoriaPacienteViewModel by viewModels()
    private lateinit var binding: FragmentHistoriaPacienteBinding
    private val args: HistoriaPacienteFragmentArgs by navArgs()
    private lateinit var eventoAdapter: EventoHistoriaClinicaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoriaPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        // Inicializar con el ID del paciente
        viewModel.inicializarHistorial(args.idPaciente)
    }

    private fun setupRecyclerView(): Unit {
        eventoAdapter = EventoHistoriaClinicaAdapter(
            eventos = emptyList()
        ) { evento ->
            // Navegar a detalles del evento
            // TODO: Implementar navegación a detalles
        }
        
        binding.rvHistoriaClinica.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventoAdapter
        }
    }

    private fun setupObservers(): Unit {
        // Observar estado del paciente
        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            paciente?.let {
                binding.tvNombreCompleto.text = "${it.nombres} ${it.apellidos}"
                binding.tvCedula.text = "Cédula: ${it.cedula}"
                binding.tvGenero.text = "Género: ${it.genero}"
                binding.tvFechaNacimiento.text = "Fecha de nacimiento: ${it.fechaNacimiento}"
                // Calcular y mostrar edad
                val edad = Utils.calcularEdadDetallada(it.fechaNacimiento)
                binding.tvEdad.text = "Edad: ${edad.anios} años, ${edad.meses} meses y ${edad.dias} días"
            }
        }

        // Observar estado del historial
        viewModel.estadoHistorial.observe(viewLifecycleOwner) { estado ->
            binding.progressBar.isVisible = estado.isLoading
            binding.layoutEstadoVacio.isVisible = estado.eventos.isEmpty() && !estado.isLoading && estado.error == null
            
            if (estado.eventos.isNotEmpty()) {
                eventoAdapter.updateEventos(estado.eventos)
            }
            
            estado.error?.let { error ->
                mostrarErrorConAccion(error)
            }
        }

        // Observar cambios en la búsqueda
        viewModel.estadoHistorial.observe(viewLifecycleOwner) { estado ->
            // Los eventos ya vienen ordenados cronológicamente desde el ViewModel
            // No necesitamos lógica adicional de filtros aquí
        }
    }

    private fun setupListeners(): Unit {
        // Configurar búsqueda
        binding.etBusqueda.addTextChangedListener { text ->
            viewModel.buscarTexto(text?.toString()?.takeIf { it.isNotBlank() })
        }
    }


    
    private fun mostrarErrorConAccion(error: String) {
        val snackbar = Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
        
        // Agregar acción de reintento para errores de conexión
        if (error.contains("conexión", ignoreCase = true) || 
            error.contains("internet", ignoreCase = true) ||
            error.contains("tardó demasiado", ignoreCase = true)) {
            
            snackbar.setAction("Reintentar") {
                viewModel.inicializarHistorial(args.idPaciente)
            }
        }
        
        snackbar.show()
    }

    override fun onDestroyView(): Unit {
        super.onDestroyView()
    }

}