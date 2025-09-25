package com.nutrizulia.presentation.view.paciente

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentResumenMedicoBinding
import com.nutrizulia.presentation.viewmodel.paciente.ResumenMedicoViewModel
import com.nutrizulia.util.Utils.calcularEdadDetallada
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ResumenMedicoFragment : Fragment() {

    private val viewModel: ResumenMedicoViewModel by viewModels()
    private lateinit var binding: FragmentResumenMedicoBinding
    private val args: ResumenMedicoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResumenMedicoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.onCreate(args.pacienteId)
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { mensaje ->
                mostrarSnackbar(requireView(), mensaje)
            }
        }

        viewModel.salir.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { salir ->
                if (salir) findNavController().popBackStack()
            }
        }

        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            binding.tvNombreCompleto.text = "${paciente.nombres} ${paciente.apellidos}"
            binding.tvCedula.text = "Cédula: ${paciente.cedula}"
            binding.tvGenero.text = "Género: ${paciente.genero}"
            binding.tvFechaNacimiento.text = "Fecha de nacimiento: ${paciente.fechaNacimiento}"
            val edad = calcularEdadDetallada(paciente.fechaNacimiento)
            binding.tvEdad.text = "Edad: ${edad.anios} años, ${edad.meses} meses y ${edad.dias} días"
        }

        viewModel.antropometricos.observe(viewLifecycleOwner) { detalle ->
            if (detalle != null) {
                binding.tvSinDatosAntropometricos.visibility = View.GONE
                // Peso
                if (detalle.peso != null) {
                    binding.tvPeso.text = "Peso: ${detalle.peso} kg"
                    binding.tvPeso.visibility = View.VISIBLE
                } else {
                    binding.tvPeso.visibility = View.GONE
                }
                
                // Altura
                if (detalle.altura != null) {
                    binding.tvAltura.text = "Altura: ${detalle.altura} cm"
                    binding.tvAltura.visibility = View.VISIBLE
                } else {
                    binding.tvAltura.visibility = View.GONE
                }
                
                // Talla
                if (detalle.talla != null) {
                    binding.tvTalla.text = "Talla: ${detalle.talla} cm"
                    binding.tvTalla.visibility = View.VISIBLE
                } else {
                    binding.tvTalla.visibility = View.GONE
                }
                
                // Circunferencia Braquial
                if (detalle.circunferenciaBraquial != null) {
                    binding.tvCircunferenciaBraquial.text = "Circunferencia braquial: ${detalle.circunferenciaBraquial} cm"
                    binding.tvCircunferenciaBraquial.visibility = View.VISIBLE
                } else {
                    binding.tvCircunferenciaBraquial.visibility = View.GONE
                }
                
                // Circunferencia Cadera
                if (detalle.circunferenciaCadera != null) {
                    binding.tvCircunferenciaCadera.text = "Circunferencia cadera: ${detalle.circunferenciaCadera} cm"
                    binding.tvCircunferenciaCadera.visibility = View.VISIBLE
                } else {
                    binding.tvCircunferenciaCadera.visibility = View.GONE
                }
                
                // Circunferencia Cintura
                if (detalle.circunferenciaCintura != null) {
                    binding.tvCircunferenciaCintura.text = "Circunferencia cintura: ${detalle.circunferenciaCintura} cm"
                    binding.tvCircunferenciaCintura.visibility = View.VISIBLE
                } else {
                    binding.tvCircunferenciaCintura.visibility = View.GONE
                }
                
                // Perímetro Cefálico
                if (detalle.perimetroCefalico != null) {
                    binding.tvPerimetroCefalico.text = "Perímetro cefálico: ${detalle.perimetroCefalico} cm"
                    binding.tvPerimetroCefalico.visibility = View.VISIBLE
                } else {
                    binding.tvPerimetroCefalico.visibility = View.GONE
                }
                
                // Pliegue Tricipital
                if (detalle.pliegueTricipital != null) {
                    binding.tvPliegueTricipital.text = "Pliegue tricipital: ${detalle.pliegueTricipital} mm"
                    binding.tvPliegueTricipital.visibility = View.VISIBLE
                } else {
                    binding.tvPliegueTricipital.visibility = View.GONE
                }
                
                // Pliegue Subescapular
                if (detalle.pliegueSubescapular != null) {
                    binding.tvPliegueSubescapular.text = "Pliegue subescapular: ${detalle.pliegueSubescapular} mm"
                    binding.tvPliegueSubescapular.visibility = View.VISIBLE
                } else {
                    binding.tvPliegueSubescapular.visibility = View.GONE
                }
                
                binding.tvUltFechaAntropometricos.text = detalle.updatedAt.toLocalDate().toString()
            }
        }

        viewModel.metabolicos.observe(viewLifecycleOwner) { detalle ->
            if (detalle != null) {
                binding.tvSinDatosMetabolicos.visibility = View.GONE
                
                // Glicemia Basal
                if (detalle.glicemiaBasal != null) {
                    binding.tvGlicemia.text = "Glicemia basal: ${detalle.glicemiaBasal} mg/dL"
                    binding.tvGlicemia.visibility = View.VISIBLE
                } else {
                    binding.tvGlicemia.visibility = View.GONE
                }
                
                // Glicemia Post Prandial
                if (detalle.glicemiaPostprandial != null) {
                    binding.tvGlicemiaPostPandrial.text = "Glicemia postprandial: ${detalle.glicemiaPostprandial} mg/dL"
                    binding.tvGlicemiaPostPandrial.visibility = View.VISIBLE
                } else {
                    binding.tvGlicemiaPostPandrial.visibility = View.GONE
                }
                
                // Glicemia Aleatoria
                if (detalle.glicemiaAleatoria != null) {
                    binding.tvGlicemiaAleatoria.text = "Glicemia aleatoria: ${detalle.glicemiaAleatoria} mg/dL"
                    binding.tvGlicemiaAleatoria.visibility = View.VISIBLE
                } else {
                    binding.tvGlicemiaAleatoria.visibility = View.GONE
                }
                
                // Hemoglobina Glicosilada
                if (detalle.hemoglobinaGlicosilada != null) {
                    binding.tvHemoglobinaGlicosilada.text = "Hemoglobina glicosilada: ${detalle.hemoglobinaGlicosilada}%"
                    binding.tvHemoglobinaGlicosilada.visibility = View.VISIBLE
                } else {
                    binding.tvHemoglobinaGlicosilada.visibility = View.GONE
                }
                
                // Triglicéridos
                if (detalle.trigliceridos != null) {
                    binding.tvTrigliceridos.text = "Triglicéridos: ${detalle.trigliceridos} mg/dL"
                    binding.tvTrigliceridos.visibility = View.VISIBLE
                } else {
                    binding.tvTrigliceridos.visibility = View.GONE
                }
                
                // Colesterol Total
                if (detalle.colesterolTotal != null) {
                    binding.tvColesterolTotal.text = "Colesterol total: ${detalle.colesterolTotal} mg/dL"
                    binding.tvColesterolTotal.visibility = View.VISIBLE
                } else {
                    binding.tvColesterolTotal.visibility = View.GONE
                }
                
                // Colesterol HDL
                if (detalle.colesterolHdl != null) {
                    binding.tvColesterolHdl.text = "Colesterol HDL: ${detalle.colesterolHdl} mg/dL"
                    binding.tvColesterolHdl.visibility = View.VISIBLE
                } else {
                    binding.tvColesterolHdl.visibility = View.GONE
                }
                
                // Colesterol LDL
                if (detalle.colesterolLdl != null) {
                    binding.tvColesterolLdl.text = "Colesterol LDL: ${detalle.colesterolLdl} mg/dL"
                    binding.tvColesterolLdl.visibility = View.VISIBLE
                } else {
                    binding.tvColesterolLdl.visibility = View.GONE
                }
                
                binding.tvUltFechaMetabolicos.text = detalle.updatedAt.toLocalDate().toString()
            }
        }

        viewModel.vitales.observe(viewLifecycleOwner) { detalle ->
            if (detalle != null) {
                binding.tvSinSignosVitales.visibility = View.GONE
                
                // Tensión Arterial Sistólica
                if (detalle.tensionArterialSistolica != null) {
                    binding.tvTensionArterialSistolica.text = "Tensión arterial sistólica: ${detalle.tensionArterialSistolica} mmHg"
                    binding.tvTensionArterialSistolica.visibility = View.VISIBLE
                } else {
                    binding.tvTensionArterialSistolica.visibility = View.GONE
                }
                
                // Tensión Arterial Diastólica
                if (detalle.tensionArterialDiastolica != null) {
                    binding.tvTensionArterialDiastolica.text = "Tensión arterial diastólica: ${detalle.tensionArterialDiastolica} mmHg"
                    binding.tvTensionArterialDiastolica.visibility = View.VISIBLE
                } else {
                    binding.tvTensionArterialDiastolica.visibility = View.GONE
                }
                
                // Frecuencia Cardíaca
                if (detalle.frecuenciaCardiaca != null) {
                    binding.tvFrecuenciaCardiaca.text = "Frecuencia cardíaca: ${detalle.frecuenciaCardiaca} lpm"
                    binding.tvFrecuenciaCardiaca.visibility = View.VISIBLE
                } else {
                    binding.tvFrecuenciaCardiaca.visibility = View.GONE
                }
                
                // Frecuencia Respiratoria
                if (detalle.frecuenciaRespiratoria != null) {
                    binding.tvFrecuenciaRespiratoria.text = "Frecuencia respiratoria: ${detalle.frecuenciaRespiratoria} rpm"
                    binding.tvFrecuenciaRespiratoria.visibility = View.VISIBLE
                } else {
                    binding.tvFrecuenciaRespiratoria.visibility = View.GONE
                }
                
                // Temperatura
                if (detalle.temperatura != null) {
                    binding.tvTemperatura.text = "Temperatura: ${detalle.temperatura} °C"
                    binding.tvTemperatura.visibility = View.VISIBLE
                } else {
                    binding.tvTemperatura.visibility = View.GONE
                }
                
                // Saturación de Oxígeno
                if (detalle.saturacionOxigeno != null) {
                    binding.tvSaturacionOxigeno.text = "Saturación de oxígeno: ${detalle.saturacionOxigeno}%"
                    binding.tvSaturacionOxigeno.visibility = View.VISIBLE
                } else {
                    binding.tvSaturacionOxigeno.visibility = View.GONE
                }
                
                // Pulso
                if (detalle.pulso != null) {
                    binding.tvPulso.text = "Pulso: ${detalle.pulso} lpm"
                    binding.tvPulso.visibility = View.VISIBLE
                } else {
                    binding.tvPulso.visibility = View.GONE
                }

                binding.tvUltFechaVitales.text = detalle.updatedAt.toLocalDate().toString()
            }
        }

        viewModel.obstetricos.observe(viewLifecycleOwner) { detalle ->
            if (detalle != null) {
                binding.tvSinDatosObstetricos.visibility = View.GONE
                
                // Está Embarazada
                if (detalle.estaEmbarazada != null) {
                    val embarazadaText = if (detalle.estaEmbarazada == true) "Sí" else "No"
                    binding.tvEstaEmbarazada.text = "¿Está embarazada?: $embarazadaText"
                    binding.tvEstaEmbarazada.visibility = View.VISIBLE
                } else {
                    binding.tvEstaEmbarazada.visibility = View.GONE
                }
                
                // Fecha Última Menstruación
                if (detalle.fechaUltimaMenstruacion != null) {
                    binding.tvFechaUltimaMenstruacion.text = "Fecha última menstruación: ${detalle.fechaUltimaMenstruacion}"
                    binding.tvFechaUltimaMenstruacion.visibility = View.VISIBLE
                } else {
                    binding.tvFechaUltimaMenstruacion.visibility = View.GONE
                }
                
                // Semanas de Gestación
                if (detalle.semanasGestacion != null) {
                    binding.tvSemanasGestacion.text = "Semanas de gestación: ${detalle.semanasGestacion} semanas"
                    binding.tvSemanasGestacion.visibility = View.VISIBLE
                } else {
                    binding.tvSemanasGestacion.visibility = View.GONE
                }
                
                // Peso Pre Embarazo
                if (detalle.pesoPreEmbarazo != null) {
                    binding.tvPesoPreEmbarazo.text = "Peso pre-embarazo: ${detalle.pesoPreEmbarazo} kg"
                    binding.tvPesoPreEmbarazo.visibility = View.VISIBLE
                } else {
                    binding.tvPesoPreEmbarazo.visibility = View.GONE
                }
                
                binding.tvUltFechaObstetricos.text = detalle.updatedAt.toLocalDate().toString()
            }
        }

        viewModel.pediatricos.observe(viewLifecycleOwner) { detalle ->
            if (detalle != null) {
                binding.tvSinDatosPediatricos.visibility = View.GONE
                
                // Usa Biberón
                if (detalle.usaBiberon != null) {
                    val biberonText = if (detalle.usaBiberon == true) "Sí" else "No"
                    binding.tvUsaBiberon.text = "¿Usa biberón?: $biberonText"
                    binding.tvUsaBiberon.visibility = View.VISIBLE
                } else {
                    binding.tvUsaBiberon.visibility = View.GONE
                }
                
                // Tipo de Lactancia
                if (detalle.tipoLactancia != null) {
                    binding.tvTipoLactancia.text = "Tipo de lactancia: ${detalle.tipoLactancia}"
                    binding.tvTipoLactancia.visibility = View.VISIBLE
                } else {
                    binding.tvTipoLactancia.visibility = View.GONE
                }

                binding.tvUltFechaPediatrico.text = detalle.updatedAt.toLocalDate().toString()
            }
        }

    }

}