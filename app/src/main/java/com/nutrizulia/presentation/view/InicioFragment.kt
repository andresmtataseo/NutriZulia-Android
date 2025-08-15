package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nutrizulia.databinding.FragmentInicioBinding
import com.nutrizulia.presentation.viewmodel.InicioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InicioFragment : Fragment() {

    private val viewModel: InicioViewModel by viewModels()
    private lateinit var binding: FragmentInicioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupClickListeners()
        
        // Cargar datos iniciales
        viewModel.loadDashboardData()
    }

    private fun setupObservers() {
        viewModel.proximaConsulta.observe(viewLifecycleOwner) { consulta ->
            consulta?.let {
                binding.tvNombrePacienteProximaConsulta.text = it.nombrePaciente
                binding.tvFechaHoraProximaConsulta.text = it.fechaHora
            }
        }

        viewModel.resumenMensual.observe(viewLifecycleOwner) { resumen ->
            resumen?.let {
                binding.tvTotalConsultas.text = it.totalConsultas.toString()
                binding.tvTotalHombres.text = it.totalHombres.toString()
                binding.tvTotalMujeres.text = it.totalMujeres.toString()
                binding.tvTotalNinos.text = it.totalNinos.toString()
                binding.tvTotalNinas.text = it.totalNinas.toString()
            }
        }

        viewModel.datosUsuario.observe(viewLifecycleOwner) { datos ->
            datos?.let {
                binding.tvBienvenidaUsuario.text = it.nombreUsuario
                binding.tvInstitucionActual.text = it.nombreInstitucion
            }
        }

        viewModel.notificacionesPendientes.observe(viewLifecycleOwner) { notificaciones ->
            // TODO: Actualizar UI cuando se implemente la tarjeta de notificaciones
        }

        viewModel.citasDelDia.observe(viewLifecycleOwner) { citas ->
            citas?.let {
                binding.tvCitasProgramadas.text = it.programadas.toString()
                binding.tvCitasCompletadas.text = it.completadas.toString()
            }
        }

        viewModel.archivosPendientes.observe(viewLifecycleOwner) { archivos ->
            binding.tvArchivosPendientes.text = archivos.toString()
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Aquí puedes mostrar/ocultar un indicador de carga si es necesario
        }
    }

    private fun setupClickListeners() {
        binding.btnVerProximaConsulta.setOnClickListener {
            // Navegar a los detalles de la próxima consulta
            // findNavController().navigate(R.id.action_inicioFragment_to_consultaDetailFragment)
        }

        binding.btnSincronizar.setOnClickListener {
            // Iniciar proceso de sincronización
            viewModel.sincronizarArchivos()
        }

    }

}