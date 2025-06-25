package com.nutrizulia.presentation.view

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentVerConsultaBinding
//import com.nutrizulia.presentation.viewmodel.VerConsultaViewModel
import com.nutrizulia.util.Utils.calcularEdad
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerConsultaFragment : Fragment() {

//    private val viewModel: VerConsultaViewModel by viewModels()
    private lateinit var binding: FragmentVerConsultaBinding
    private val args: VerConsultaFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVerConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.obtenerPaciente(args.idConsulta)
//
//        viewModel.consultaConPacienteYSignosVitales.observe(viewLifecycleOwner) {
//            binding.tfNombreCompletoPaciente.editText?.setText("${it.paciente.primerNombre} ${it.paciente.segundoNombre} ${it.paciente.primerApellido} ${it.paciente.segundoApellido}")
//            binding.tfEdadPaciente.editText?.setText("${calcularEdad(it.paciente.fechaNacimiento)}")
//            binding.tfGeneroPaciente.editText?.setText(it.paciente.genero)
//
//            binding.tfPeso.editText?.setText(it.signosVitales.peso.toString())
//            binding.tfAltura.editText?.setText(it.signosVitales.altura.toString())
//
//           if (it.signosVitales.glicemiaBasal != null) {
//               binding.layoutGlicemiaBasal.visibility = View.VISIBLE
//               binding.tfGlicemiaBasal.editText?.setText(it.signosVitales.glicemiaBasal.toString())
//           }
//            if (it.signosVitales.glicemiaPostprandial != null) {
//                binding.layoutGlicemiaPostprandial.visibility = View.VISIBLE
//                binding.tfGlicemiaPostprandial.editText?.setText(it.signosVitales.glicemiaPostprandial.toString())
//            }
//            if (it.signosVitales.glicemiaAleatoria != null) {
//                binding.layoutGlicemiaAleatoria.visibility = View.VISIBLE
//                binding.tfGlicemiaAleatoria.editText?.setText(it.signosVitales.glicemiaAleatoria.toString())
//            }
//            if (it.signosVitales.hemoglobinaGlicosilada != null) {
//                binding.layoutHemoglobinaGlicosilada.visibility = View.VISIBLE
//                binding.tfHemoglobinaGlicosilada.editText?.setText(it.signosVitales.hemoglobinaGlicosilada.toString())
//            }
//            if (it.signosVitales.trigliceridos != null) {
//                binding.layoutTrigliceridos.visibility = View.VISIBLE
//                binding.tfTrigliceridos.editText?.setText(it.signosVitales.trigliceridos.toString())
//            }
//            if (it.signosVitales.colesterolTotal != null) {
//                binding.layoutColesterolTotal.visibility = View.VISIBLE
//                binding.tfColesterolTotal.editText?.setText(it.signosVitales.colesterolTotal.toString())
//            }
//            if (it.signosVitales.colesterolHdl != null) {
//                binding.layoutColesterolHdl.visibility = View.VISIBLE
//                binding.tfColesterolHdl.editText?.setText(it.signosVitales.colesterolHdl.toString())
//            }
//            if (it.signosVitales.colesterolLdl != null) {
//                binding.layoutColesterolLdl.visibility = View.VISIBLE
//                binding.tfColesterolLdl.editText?.setText(it.signosVitales.colesterolLdl.toString())
//            }
//            if (it.signosVitales.tensionArterial != null) {
//                binding.layoutTensionArterial.visibility = View.VISIBLE
//                binding.tfTensionArterial.editText?.setText(it.signosVitales.tensionArterial.toString())
//            }
//            if (it.signosVitales.frecuenciaCardiaca != null) {
//                binding.layoutFrecuenciaCardiaca.visibility = View.VISIBLE
//                binding.tfFrecuenciaCardiaca.editText?.setText(it.signosVitales.frecuenciaCardiaca.toString())
//            }
//            if (it.signosVitales.pulso != null) {
//                binding.layoutPulso.visibility = View.VISIBLE
//                binding.tfPulso.editText?.setText(it.signosVitales.pulso.toString())
//            }
//            if (it.signosVitales.saturacionOxigeno != null) {
//                binding.layoutSTO2.visibility = View.VISIBLE
//                binding.tfSTO2.editText?.setText(it.signosVitales.saturacionOxigeno.toString())
//            }
//            if (it.signosVitales.temperatura != null) {
//                binding.layoutTemperatura.visibility = View.VISIBLE
//                binding.tfTemperatura.editText?.setText(it.signosVitales.temperatura.toString())
//            }
//            if (it.signosVitales.frecuenciaRespiratoria != null) {
//                binding.layoutFrecuenciaRespiratoria.visibility = View.VISIBLE
//                binding.tfFrecuenciaRespiratoria.editText?.setText(it.signosVitales.frecuenciaRespiratoria.toString())
//            }
//            if (it.signosVitales.circunferenciaBraquial != null) {
//                binding.layoutCircuferenciaBraquial.visibility = View.VISIBLE
//                binding.tfCircuferenciaBraquial.editText?.setText(it.signosVitales.circunferenciaBraquial.toString())
//            }
//            if (it.signosVitales.circunferenciaCadera != null) {
//                binding.layoutCircuferenciaCadera.visibility = View.VISIBLE
//                binding.tfCircuferenciaCadera.editText?.setText(it.signosVitales.circunferenciaCadera.toString())
//            }
//            if (it.signosVitales.circunferenciaCintura != null) {
//                binding.layoutCircuferenciaCintura.visibility = View.VISIBLE
//                binding.tfCircuferenciaCintura.editText?.setText(it.signosVitales.circunferenciaCintura.toString())
//            }
//            if (it.signosVitales.perimetroCefalico != null) {
//                binding.layoutPerimetroCefalico.visibility = View.VISIBLE
//                binding.tfPerimetroCefalico.editText?.setText(it.signosVitales.perimetroCefalico.toString())
//            }
//            if (it.signosVitales.isEmbarazo != null) {
//                val esEmbarazo:String
//                if (it.signosVitales.isEmbarazo) {
//                    esEmbarazo = "Sí"
//                    binding.layoutSiEmbarazo.visibility = View.VISIBLE
//                    binding.tfFechaUltimaMenstruacion.editText?.setText(it.signosVitales.fechaUltimaMenstruacion.toString())
//                    binding.tfSemanasGestacion.editText?.setText(it.signosVitales.semanasGestacion.toString())
//                    binding.tfPesoPreEmbarazo.editText?.setText(it.signosVitales.pesoPreEmbarazo.toString())
//                } else {
//                    esEmbarazo = "No"
//                }
//                binding.layoutEmbarazo.visibility = View.VISIBLE
//                binding.tfIsEmbarazo.editText?.setText(esEmbarazo)
//            }
//
//            binding.tfDiagnosticoPrincipal.editText?.setText(it.consulta.diagnosticoPrincipal.toString())
//            binding.tfDiagnosticoSecundario.editText?.setText(it.consulta.diagnosticoSecundario.toString())
//            binding.tfObservaciones.editText?.setText(it.consulta.observaciones.toString())
//        }
//
//        viewModel.isLoading.observe(viewLifecycleOwner) { binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE }
//        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(requireView(), it) }
//        viewModel.salir.observe(viewLifecycleOwner) { if (it) findNavController().popBackStack() }
    }

}