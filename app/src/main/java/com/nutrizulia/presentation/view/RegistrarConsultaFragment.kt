package com.nutrizulia.presentation.view

import android.content.DialogInterface
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private val listSignosVitales = arrayOf("Glicemia Basal", "Glicemia Postprandial", "Glicemia Aleatoria", "Hemoglobina Glicosilada" ,"Triglicéridos", "Colesterol Total", "Colesterol HDL", "Colesterol LDL", "Tensión arterial", "Frecuencia cardiaca", "Pulso", "Saturación de oxigeno", "Frecuencia respiratoria", "Temperatura", "Circuferencia braquial", "Circuferencia cadera", "Circuferencia cintura", "Perimetro cefálico")
    private lateinit var mapSignosVitales: Map<String, LinearLayout>
    private lateinit var stateSignosVitales: BooleanArray

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
        configurarDropdownEmbarazada()
        mapSignosVitales = mapOf(
            listSignosVitales[0] to binding.layoutGlicemiaBasal,
            listSignosVitales[1] to binding.layoutGlicemiaPostprandial,
            listSignosVitales[2] to binding.layoutGlicemiaAleatoria,
            listSignosVitales[3] to binding.layoutHemoglobinaGlicosilada,
            listSignosVitales[4] to binding.layoutTrigliceridos,
            listSignosVitales[5] to binding.layoutColesterolTotal,
            listSignosVitales[6] to binding.layoutColesterolHdl,
            listSignosVitales[7] to binding.layoutColesterolLdl,
            listSignosVitales[8] to binding.layoutTensionArterial,
            listSignosVitales[9] to binding.layoutFrecuenciaCardiaca,
            listSignosVitales[10] to binding.layoutPulso,
            listSignosVitales[11] to binding.layoutSTO2,
            listSignosVitales[12] to binding.layoutFrecuenciaRespiratoria,
            listSignosVitales[13] to binding.layoutTemperatura,
            listSignosVitales[14] to binding.layoutCircuferenciaBraquial,
            listSignosVitales[15] to binding.layoutCircuferenciaCadera,
            listSignosVitales[16] to binding.layoutCircuferenciaCintura,
            listSignosVitales[17] to binding.layoutPerimetroCefalico
        )

        stateSignosVitales = BooleanArray(listSignosVitales.size) { index ->
            mapSignosVitales[listSignosVitales[index]]?.visibility == View.VISIBLE
        }

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
                if (citaConPaciente.paciente.genero == "FEMENINO") {
                    binding.layoutEmbarazo.visibility = View.VISIBLE
                }
            }
        }

        binding.btnRegistrarConsulta.setOnClickListener {
            registrarConsulta()
        }

        binding.btnAgregarDato.setOnClickListener {
        showVitalSignSelectionDialog()
        }

        binding.btnRemoverGlicemiaBasal.setOnClickListener {
            binding.layoutGlicemiaBasal.visibility = View.GONE
            binding.tfGlicemiaBasal.editText?.text = null
        }
        binding.btnRemoverGlicemiaPostprandial.setOnClickListener {
            binding.layoutGlicemiaPostprandial.visibility = View.GONE
            binding.tfGlicemiaPostprandial.editText?.text = null
        }
        binding.btnRemoverGlicemiaAleatoria.setOnClickListener {
            binding.layoutGlicemiaAleatoria.visibility = View.GONE
            binding.tfGlicemiaAleatoria.editText?.text = null
        }
        binding.btnRemoverHemoglobinaGlicosilada.setOnClickListener {
            binding.layoutHemoglobinaGlicosilada.visibility = View.GONE
            binding.tfHemoglobinaGlicosilada.editText?.text = null
        }
        binding.btnRemoverTrigliceridos.setOnClickListener {
            binding.layoutTrigliceridos.visibility = View.GONE
            binding.tfTrigliceridos.editText?.text = null
        }
        binding.btnRemoverColesterolTotal.setOnClickListener {
            binding.layoutColesterolTotal.visibility = View.GONE
            binding.tfColesterolTotal.editText?.text = null
        }
        binding.btnRemoverColesterolHdl.setOnClickListener {
            binding.layoutColesterolHdl.visibility = View.GONE
            binding.tfColesterolHdl.editText?.text = null
        }
        binding.btnRemoverColesterolLdl.setOnClickListener {
            binding.layoutColesterolLdl.visibility = View.GONE
            binding.tfColesterolLdl.editText?.text = null
        }
        binding.btnRemoverTensionArterial.setOnClickListener {
            binding.layoutTensionArterial.visibility = View.GONE
            binding.tfTensionArterial.editText?.text = null
        }
        binding.btnRemoverFrecuenciaCardiaca.setOnClickListener {
            binding.layoutFrecuenciaCardiaca.visibility = View.GONE
            binding.tfFrecuenciaCardiaca.editText?.text = null
        }
        binding.btnRemoverPulso.setOnClickListener {
            binding.layoutPulso.visibility = View.GONE
            binding.tfPulso.editText?.text = null
        }
        binding.btnRemoverSTO2.setOnClickListener {
            binding.layoutSTO2.visibility = View.GONE
            binding.tfSTO2.editText?.text = null
        }
        binding.btnRemoverTemperatura.setOnClickListener {
            binding.layoutTemperatura.visibility = View.GONE
            binding.tfTemperatura.editText?.text = null
        }
        binding.btnRemoverFrecuenciaRespiratoria.setOnClickListener {
            binding.layoutFrecuenciaRespiratoria.visibility = View.GONE
            binding.tfFrecuenciaRespiratoria.editText?.text = null
        }
        binding.btnRemoverCircuferenciaBraquial.setOnClickListener {
            binding.layoutCircuferenciaBraquial.visibility = View.GONE
            binding.tfCircuferenciaBraquial.editText?.text = null
        }
        binding.btnRemoverCircuferenciaCadera.setOnClickListener {
            binding.layoutCircuferenciaCadera.visibility = View.GONE
            binding.tfCircuferenciaCadera.editText?.text = null
        }
        binding.btnRemoverCircuferenciaCintura.setOnClickListener {
            binding.layoutCircuferenciaCintura.visibility = View.GONE
            binding.tfCircuferenciaCintura.editText?.text = null
        }
        binding.btnRemoverPerimetroCefalico.setOnClickListener {
            binding.layoutPerimetroCefalico.visibility = View.GONE
            binding.tfPerimetroCefalico.editText?.text = null
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
            diagnosticoPrincipal = obtenerTexto(binding.tfDiagnosticoPrincipal) ?: "",
            diagnosticoSecundario = obtenerTexto(binding.tfDiagnosticoSecundario) ?: "",
            observaciones = obtenerTexto(binding.tfObservaciones) ?: ""
        )
        val signosVitales = SignosVitales(
            consultaId = 0,
            peso = obtenerTexto(binding.tfPeso)?.toDoubleOrNull() ?: 0.0,
            altura = obtenerTexto(binding.tfAltura)?.toDoubleOrNull() ?: 0.0,
            glicemiaBasal = obtenerTexto(binding.tfGlicemiaBasal)?.toIntOrNull(),
            glicemiaPostprandial = obtenerTexto(binding.tfGlicemiaPostprandial)?.toIntOrNull(),
            glicemiaAleatoria = obtenerTexto(binding.tfGlicemiaAleatoria)?.toIntOrNull(),
            hemoglobinaGlicosilada = obtenerTexto(binding.tfHemoglobinaGlicosilada)?.toDoubleOrNull(),
            trigliceridos = obtenerTexto(binding.tfTrigliceridos)?.toIntOrNull(),
            colesterolTotal = obtenerTexto(binding.tfColesterolTotal)?.toIntOrNull(),
            colesterolHdl = obtenerTexto(binding.tfColesterolHdl)?.toIntOrNull(),
            colesterolLdl = obtenerTexto(binding.tfColesterolLdl)?.toIntOrNull(),
            tensionArterial = obtenerTexto(binding.tfTensionArterial),
            frecuenciaCardiaca = obtenerTexto(binding.tfFrecuenciaCardiaca)?.toIntOrNull(),
            pulso = obtenerTexto(binding.tfPulso)?.toIntOrNull(),
            saturacionOxigeno = obtenerTexto(binding.tfSTO2)?.toIntOrNull(),
            frecuenciaRespiratoria = obtenerTexto(binding.tfFrecuenciaRespiratoria)?.toIntOrNull(),
            temperatura = obtenerTexto(binding.tfTemperatura)?.toDoubleOrNull(),
            circunferenciaBraquial = obtenerTexto(binding.tfCircuferenciaBraquial)?.toDoubleOrNull(),
            circunferenciaCadera = obtenerTexto(binding.tfCircuferenciaCadera)?.toDoubleOrNull(),
            circunferenciaCintura = obtenerTexto(binding.tfCircuferenciaCintura)?.toDoubleOrNull(),
            perimetroCefalico = obtenerTexto(binding.tfPerimetroCefalico)?.toDoubleOrNull(),
            isEmbarazo = if (obtenerTexto(binding.tfIsEmbarazo) == "Si") true else null,
            fechaUltimaMenstruacion = obtenerTexto(binding.tfFechaUltimaMenstruacion),
            semanasGestacion = obtenerTexto(binding.tfSemanasGestacion)?.toIntOrNull(),
            pesoPreEmbarazo = obtenerTexto(binding.tfPesoPreEmbarazo)?.toDoubleOrNull(),
            isTetero = null,
            tipoLactancia = null
        )

        viewModel.registrarConsulta(consultaNueva, signosVitales)
    }

    private fun showVitalSignSelectionDialog() {
        stateSignosVitales = BooleanArray(listSignosVitales.size) { index ->
            mapSignosVitales[listSignosVitales[index]]?.visibility == View.VISIBLE
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona los signos vitales")
            .setMultiChoiceItems(
                listSignosVitales,
                stateSignosVitales
            ) { dialog, which, isChecked ->
                stateSignosVitales[which] = isChecked
            }
            .setPositiveButton("Agregar") { dialog, which ->
                for (i in listSignosVitales.indices) {
                    val view = mapSignosVitales[listSignosVitales[i]]
                    if (view != null) {
                        if (stateSignosVitales[i]) {
                            view.visibility = View.VISIBLE
                        } else {
                            view.visibility = View.GONE
                            when (i) {
                                0 -> binding.tfGlicemiaBasal.editText?.text = null
                                1 -> binding.tfGlicemiaPostprandial.editText?.text = null
                                2 -> binding.tfGlicemiaAleatoria.editText?.text = null
                                3 -> binding.tfHemoglobinaGlicosilada.editText?.text = null
                                4 -> binding.tfTrigliceridos.editText?.text = null
                                5 -> binding.tfColesterolTotal.editText?.text = null
                                6 -> binding.tfColesterolHdl.editText?.text = null
                                7 -> binding.tfColesterolLdl.editText?.text = null
                                8 -> binding.tfTensionArterial.editText?.text = null
                                9 -> binding.tfFrecuenciaCardiaca.editText?.text = null
                                10 -> binding.tfPulso.editText?.text = null
                                11 -> binding.tfSTO2.editText?.text = null
                                12 -> binding.tfFrecuenciaRespiratoria.editText?.text = null
                                13 -> binding.tfTemperatura.editText?.text = null
                                14 -> binding.tfCircuferenciaBraquial.editText?.text = null
                                15 -> binding.tfCircuferenciaCadera.editText?.text = null
                                16 -> binding.tfCircuferenciaCintura.editText?.text = null
                                17 -> binding.tfPerimetroCefalico.editText?.text = null
                            }
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun configurarDropdownEmbarazada() {
        val dropdown = binding.tfIsEmbarazo.editText as? AutoCompleteTextView ?: return
        dropdown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.layoutSiEmbarazo.visibility = View.VISIBLE
                }
                1 -> {
                    binding.layoutSiEmbarazo.visibility = View.GONE
                    binding.tfFechaUltimaMenstruacion.editText?.text = null
                    binding.tfSemanasGestacion.editText?.text = null
                    binding.tfPesoPreEmbarazo.editText?.text = null
                }
            }
        }
    }

    private fun quitarErrores() {
        binding.tfDiagnosticoPrincipal.error = null
        binding.tfDiagnosticoSecundario.error = null
        binding.tfPeso.error = null
        binding.tfAltura.error = null
    }
}