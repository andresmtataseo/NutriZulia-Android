package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.nutrizulia.databinding.FragmentInicioBinding
import com.nutrizulia.presentation.view.paciente.AccionesPacienteFragmentDirections
import com.nutrizulia.presentation.viewmodel.InicioViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

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
        
        setupGreeting()
        setupObservers()
        setupClickListeners()
        
        // Cargar datos iniciales
        viewModel.loadDashboardData()
    }

    private fun setupObservers() {
        viewModel.proximaConsulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta != null) {
                binding.tvNombrePacienteProximaConsulta.text = consulta.nombrePaciente
                binding.tvFechaHoraProximaConsulta.text = consulta.fechaHora
                binding.layoutProximaConsulta.visibility = View.VISIBLE
            } else {
                binding.layoutProximaConsulta.visibility = View.GONE
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
                binding.tvSinPreviaCita.text = it.sinPreviaCita.toString()
                binding.tvCanceladas.text = it.canceladas.toString()
            }
        }

        viewModel.archivosPendientes.observe(viewLifecycleOwner) { archivos ->
            binding.tvArchivosPendientes.text = archivos.toString()
            // Ocultar card de sincronización si no hay archivos pendientes
            binding.cardSincronizacion.visibility = if (archivos > 0) View.VISIBLE else View.GONE
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Implementar indicador de carga cuando se añada al layout
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
        
        viewModel.citasDelDiaDetalle.observe(viewLifecycleOwner) { citasDetalle ->
            // TODO: Actualizar RecyclerView o lista de citas del día si existe en el layout
            // Por ahora solo actualizamos los contadores que ya están implementados
        }
    }

    private fun setupClickListeners() {
        binding.btnVerProximaConsulta.setOnClickListener {
            findNavController().navigate(
                InicioFragmentDirections.actionInicioFragmentToAccionesCitaFragment(viewModel.proximaConsulta.value?.consultaId
                    ?: return@setOnClickListener)
            )
        }

        binding.btnSincronizar.setOnClickListener {
            findNavController().navigate(
                InicioFragmentDirections.actionInicioFragmentToSyncBatchFragment()
            )
        }

        binding.btnCambiar.setOnClickListener {
            findNavController().navigate(
                InicioFragmentDirections.actionInicioFragmentToSeleccionarInstitucionFragment2()
            )
        }
    }

    private fun setupGreeting() {
        binding.tvSaludo.text = getDynamicGreeting()
    }

    private fun getDynamicGreeting(): String {
        val calendar: Calendar = Calendar.getInstance()
        val hourOfDay: Int = calendar.get(Calendar.HOUR_OF_DAY)
        
        return when (hourOfDay) {
            in 5..11 -> "¡Buenos días!"
            in 12..17 -> "¡Buenas tardes!"
            else -> "¡Buenas noches!"
        }
    }

}