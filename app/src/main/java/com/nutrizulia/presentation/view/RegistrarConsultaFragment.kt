package com.nutrizulia.presentation.view

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentRegistrarConsultaBinding
import com.nutrizulia.domain.model.Cita
import com.nutrizulia.domain.model.Consulta
import com.nutrizulia.domain.model.SignosVitales
import com.nutrizulia.presentation.viewmodel.RegistrarConsultaViewModel
import com.nutrizulia.util.EstadoCita
import com.nutrizulia.util.Utils.calcularEdad
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerFechaActual
import com.nutrizulia.util.Utils.obtenerHoraActual
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrarConsultaFragment : Fragment() {

    private val viewModel: RegistrarConsultaViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarConsultaBinding
    private val args: RegistrarConsultaFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cargarCitaConPaciente(args.idCita)

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            quitarErrores()
            errores.forEach { (key, message) ->
                when (key) {
                    "diagPrincipal" -> mostrarErrorEnCampo(binding.tfDiagnosticoPrincipal, message)
                    "peso" -> mostrarErrorEnCampo(binding.tfPeso, message)
                    "altura" -> mostrarErrorEnCampo(binding.tfAltura, message)
                }
            }
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack()

        }

        viewModel.citaConPaciente.observe(viewLifecycleOwner) { citaConPaciente ->
            if (citaConPaciente != null) {
                binding.tfNombreCompletoPaciente.editText?.setText("${citaConPaciente.paciente.primerNombre} ${citaConPaciente.paciente.segundoNombre} ${citaConPaciente.paciente.primerApellido} ${citaConPaciente.paciente.segundoApellido}")
                binding.tfGeneroPaciente.editText?.setText(citaConPaciente.paciente.genero)
                binding.tfEdadPaciente.editText?.setText("${calcularEdad(citaConPaciente.paciente.fechaNacimiento)}")
            }
        }

        binding.btnRegistrarConsulta.setOnClickListener {
            registrarConsulta()
        }


    }

    private fun registrarConsulta() {
        val consultaNueva = Consulta(
            usuarioId = 1,
            pacienteId = viewModel.citaConPaciente.value?.paciente?.id ?: 0,
            citaId = args.idCita,
            actividadId = null,
            fecha = obtenerFechaActual(),
            hora = obtenerHoraActual(),
            diagnosticoPrincipal = obtenerTexto(binding.tfDiagnosticoPrincipal),
            diagnosticoSecundario = obtenerTexto(binding.tfDiagnosticoSecundario),
            observaciones = obtenerTexto(binding.tfObservaciones)
        )
        val signosVitales = SignosVitales(
            consultaId = 0,
            peso = obtenerTexto(binding.tfPeso).toDoubleOrNull() ?: 0.0,
            altura = obtenerTexto(binding.tfAltura).toDoubleOrNull() ?: 0.0,
            temperatura = obtenerTexto(binding.tfTemperatura).toDoubleOrNull() ?: 0.0,
            glicemia = obtenerTexto(binding.tfGlicemia).toIntOrNull() ?: 0,
            pulso = obtenerTexto(binding.tfPulso).toIntOrNull() ?: 0,
            tensionArterial =  obtenerTexto(binding.tfTensionArterial),
            frecuenciaCardiaca = obtenerTexto(binding.tfFrecuenciaCardiaca).toIntOrNull() ?: 0,
            frecuenciaRespiratoria = obtenerTexto(binding.tfFrecuenciaRespiratoria).toIntOrNull() ?: 0,
            saturacionOxigeno = obtenerTexto(binding.tfSTO2).toIntOrNull() ?: 0,
            perimetroCefalico = 0.0,
            circunferenciaBraquial = 0.0,
            circunferenciaCintura = 0.0,
            isEmbarazo = false,
            fechaUltimaMenstruacion = "",
            semanasGestacion = 0,
            tipoLactancia = "",
            isTetero = false,
            relacionPesoAltura = 0.0,
            relacionAlturaEdad = 0.0,
            relacionPesoEdad = 0.0
        )

        viewModel.registrarConsulta(consultaNueva, signosVitales)
    }

    private fun quitarErrores() {
        binding.tfDiagnosticoPrincipal.error = null
        binding.tfDiagnosticoSecundario.error = null
        binding.tfPeso.error = null
        binding.tfAltura.error = null
    }
}