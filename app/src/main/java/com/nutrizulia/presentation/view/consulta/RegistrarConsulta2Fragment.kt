package com.nutrizulia.presentation.view.consulta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentRegistrarConsulta2Binding
import com.nutrizulia.presentation.viewmodel.RegistrarConsultaViewModel
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.getValue

@AndroidEntryPoint
class RegistrarConsulta2Fragment : Fragment() {

    private val viewModel: RegistrarConsultaViewModel by navGraphViewModels(R.id.registrarConsultaGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var binding: FragmentRegistrarConsulta2Binding
    private val args: RegistrarConsulta2FragmentArgs by navArgs()
    private var ultimaFechaSeleccionada: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarConsulta2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.cargarDatosClinicos()
        setupObservers()
        setupListeners()
        if (args.idConsulta != null && args.isEditable) {
            deshabilitarCamposConsulta()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
            binding.content.visibility = if (it) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) {
            Utils.mostrarSnackbar(binding.root, it)
        }

        viewModel.paciente.observe(viewLifecycleOwner) { paciente ->
            if (paciente.genero.equals("FEMENINO", ignoreCase = true) ) {
                binding.layoutDetallesEmbarazo.visibility = View.VISIBLE
            } else {
                binding.layoutDetallesEmbarazo.visibility = View.GONE
            }

            val edad = paciente.fechaNacimiento.let { Utils.calcularEdad(it) }
            if (edad in 0..4) {
                binding.layoutSignosPediatricos.visibility = View.VISIBLE
            } else {
                binding.layoutSignosPediatricos.visibility = View.GONE
            }
        }

        viewModel.detalleVital.observe(viewLifecycleOwner) { detalle ->
            detalle?.let {
                with(binding) {
                    tfTensionArterialSistolica.editText?.setText(it.tensionArterialSistolica?.toString() ?: "")
                    layoutTensionArterialSistolica.isVisible = it.tensionArterialSistolica != null

                    tfTensionArterialDiastolica.editText?.setText(it.tensionArterialDiastolica?.toString() ?: "")
                    layoutTensionArterialDiastolica.isVisible = it.tensionArterialDiastolica != null

                    tfFrecuenciaCardiaca.editText?.setText(it.frecuenciaCardiaca?.toString() ?: "")
                    layoutFrecuenciaCardiaca.isVisible = it.frecuenciaCardiaca != null

                    tfFrecuenciaRespiratoria.editText?.setText(it.frecuenciaRespiratoria?.toString() ?: "")
                    layoutFrecuenciaRespiratoria.isVisible = it.frecuenciaRespiratoria != null

                    tfTemperatura.editText?.setText(it.temperatura?.toString() ?: "")
                    layoutTemperatura.isVisible = it.temperatura != null

                    tfSTO2.editText?.setText(it.saturacionOxigeno?.toString() ?: "")
                    layoutSTO2.isVisible = it.saturacionOxigeno != null

                    tfPulso.editText?.setText(it.pulso?.toString() ?: "")
                    layoutPulso.isVisible = it.pulso != null
                }
            }
        }

        viewModel.detalleAntropometrico.observe(viewLifecycleOwner) { detalle ->
            detalle?.let {
                with(binding) {
                    tfPeso.editText?.setText(it.peso?.toString() ?: "")
                    layoutPeso.isVisible = it.peso != null

                    tfAltura.editText?.setText(it.altura?.toString() ?: "")
                    layoutAltura.isVisible = it.altura != null

                    tfTalla.editText?.setText(it.talla?.toString() ?: "")
                    layoutTalla.isVisible = it.talla != null

                    tfCircuferenciaBraquial.editText?.setText(it.circunferenciaBraquial?.toString() ?: "")
                    layoutCircuferenciaBraquial.isVisible = it.circunferenciaBraquial != null

                    tfCircuferenciaCadera.editText?.setText(it.circunferenciaCadera?.toString() ?: "")
                    layoutCircuferenciaCadera.isVisible = it.circunferenciaCadera != null

                    tfCircuferenciaCintura.editText?.setText(it.circunferenciaCintura?.toString() ?: "")
                    layoutCircuferenciaCintura.isVisible = it.circunferenciaCintura != null

                    tfPerimetroCefalico.editText?.setText(it.perimetroCefalico?.toString() ?: "")
                    layoutPerimetroCefalico.isVisible = it.perimetroCefalico != null

                    tfPliegueTricipital.editText?.setText(it.pliegueTricipital?.toString() ?: "")
                    layoutPliegueTricipital.isVisible = it.pliegueTricipital != null

                    tfPliegueSubescapular.editText?.setText(it.pliegueSubescapular?.toString() ?: "")
                    layoutPliegueSubescapular.isVisible = it.pliegueSubescapular != null
                }
            }
        }

        viewModel.detalleMetabolico.observe(viewLifecycleOwner) { detalle ->
            detalle?.let {
                with(binding) {
                    tfGlicemiaBasal.editText?.setText(it.glicemiaBasal?.toString() ?: "")
                    layoutGlicemiaBasal.isVisible = it.glicemiaBasal != null

                    tfGlicemiaPostprandial.editText?.setText(it.glicemiaPostprandial?.toString() ?: "")
                    layoutGlicemiaPostprandial.isVisible = it.glicemiaPostprandial != null

                    tfGlicemiaAleatoria.editText?.setText(it.glicemiaAleatoria?.toString() ?: "")
                    layoutGlicemiaAleatoria.isVisible = it.glicemiaAleatoria != null

                    tfHemoglobinaGlicosilada.editText?.setText(it.hemoglobinaGlicosilada?.toString() ?: "")
                    layoutHemoglobinaGlicosilada.isVisible = it.hemoglobinaGlicosilada != null

                    tfTrigliceridos.editText?.setText(it.trigliceridos?.toString() ?: "")
                    layoutTrigliceridos.isVisible = it.trigliceridos != null

                    tfColesterolTotal.editText?.setText(it.colesterolTotal?.toString() ?: "")
                    layoutColesterolTotal.isVisible = it.colesterolTotal != null

                    tfColesterolHdl.editText?.setText(it.colesterolHdl?.toString() ?: "")
                    layoutColesterolHdl.isVisible = it.colesterolHdl != null

                    tfColesterolLdl.editText?.setText(it.colesterolLdl?.toString() ?: "")
                    layoutColesterolLdl.isVisible = it.colesterolLdl != null
                }
            }
        }

        viewModel.detalleObstetricia.observe(viewLifecycleOwner) { detalle ->
            detalle?.let {
                val embarazo = if (it.estaEmbarazada == true) "Sí" else "No"
                (binding.tfIsEmbarazo.editText as? AutoCompleteTextView)?.setText(embarazo, false)

                binding.layoutSiEmbarazo.isVisible = it.estaEmbarazada == true

                if (it.estaEmbarazada == true) {
                    binding.tfFechaUltimaMenstruacion.editText?.setText(it.fechaUltimaMenstruacion?.toString() ?: "")
                    binding.tfSemanasGestacion.editText?.setText(it.semanasGestacion?.toString() ?: "")
                    binding.tfPesoPreEmbarazo.editText?.setText(it.pesoPreEmbarazo?.toString() ?: "")
                }
            }
        }

        viewModel.detallePediatrico.observe(viewLifecycleOwner) { detalle ->
//            detalle?.let {
//                val biberonTexto = if (it.usaBiberon == true) "Sí" else "No"
//                (binding.tfUsaBiberon.editText as? AutoCompleteTextView)?.setText(biberonTexto, false)
//
//                if (it.tipoLactancia != null) {
//                    (binding.tfTipoLactancia.editText as? AutoCompleteTextView)?.setText(it.tipoLactancia.nombre, false)
//                }
//            }
        }

        viewModel.salir.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }
    }

    private fun setupListeners() {

        configurarDropdownEmbarazada()

        mostrarSelectorFecha(binding.tfFechaUltimaMenstruacion.editText as TextInputEditText)

        binding.btnSiguiente.setOnClickListener {
            viewModel.guardarSignosVitales(
                frecuenciaCardiaca = binding.tfFrecuenciaCardiaca.editText?.text.toString().toIntOrNull(),
                presionSistolica = binding.tfTensionArterialSistolica.editText?.text.toString().toIntOrNull(),
                presionDiastolica = binding.tfTensionArterialDiastolica.editText?.text.toString().toIntOrNull(),
                frecuenciaRespiratoria = binding.tfFrecuenciaRespiratoria.editText?.text.toString().toIntOrNull(),
                temperatura = binding.tfTemperatura.editText?.text.toString().toDoubleOrNull(),
                saturacionOxigeno = binding.tfSTO2.editText?.text.toString().toIntOrNull(),
                pulso = binding.tfPulso.editText?.text.toString().toIntOrNull()
            )

            viewModel.guardarDatosAntropometricos(
                peso = binding.tfPeso.editText?.text.toString().toDoubleOrNull(),
                altura = binding.tfAltura.editText?.text.toString().toDoubleOrNull(),
                talla = binding.tfTalla.editText?.text.toString().toDoubleOrNull(),
                circunferenciaBraquial = binding.tfCircuferenciaBraquial.editText?.text.toString().toDoubleOrNull(),
                circunferenciaCadera = binding.tfCircuferenciaCadera.editText?.text.toString().toDoubleOrNull(),
                circunferenciaCintura = binding.tfCircuferenciaCintura.editText?.text.toString().toDoubleOrNull(),
                perimetroCefalico = binding.tfPerimetroCefalico.editText?.text.toString().toDoubleOrNull(),
                pliegueTricipital = binding.tfPliegueTricipital.editText?.text.toString().toDoubleOrNull(),
                pliegueSubescapular = binding.tfPliegueSubescapular.editText?.text.toString().toDoubleOrNull()
            )

            viewModel.guardarDatosMetabolicos(
                glicemiaBasal = binding.tfGlicemiaBasal.editText?.text.toString().toIntOrNull(),
                glicemiaPostprandial = binding.tfGlicemiaPostprandial.editText?.text.toString().toIntOrNull(),
                glicemiaAleatoria = binding.tfGlicemiaAleatoria.editText?.text.toString().toIntOrNull(),
                hemoglobinaGlicosilada = binding.tfHemoglobinaGlicosilada.editText?.text.toString().toDoubleOrNull(),
                trigliceridos = binding.tfTrigliceridos.editText?.text.toString().toIntOrNull(),
                colesterolTotal = binding.tfColesterolTotal.editText?.text.toString().toIntOrNull(),
                colesterolHdl = binding.tfColesterolHdl.editText?.text.toString().toIntOrNull(),
                colesterolLdl = binding.tfColesterolLdl.editText?.text.toString().toIntOrNull()
            )

            val estaEmbarazada = binding.tfIsEmbarazo.editText?.text.toString().equals("sí", ignoreCase = true)
            val fechaUltimaMenstruacion = try {
                LocalDate.parse(binding.tfFechaUltimaMenstruacion.editText?.text.toString())
            } catch (e: DateTimeParseException) {
                null
            }

            viewModel.guardarDatosObstetricos(
                estaEmbarazada = estaEmbarazada,
                fechaUltimaMenstruacion = fechaUltimaMenstruacion,
                semanasGestacion = binding.tfSemanasGestacion.editText?.text.toString().toIntOrNull(),
                pesoPreEmbarazo = binding.tfPesoPreEmbarazo.editText?.text.toString().toDoubleOrNull()
            )

            findNavController().navigate(
                RegistrarConsulta2FragmentDirections.actionRegistrarConsulta2FragmentToRegistrarConsulta3Fragment(
                    idPaciente = args.idPaciente,
                    idConsulta = args.idConsulta,
                    isEditable = args.isEditable
                )
            )
        }

        binding.btnLimpiar.setOnClickListener {
            Utils.mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Desea limpiar todos los campos?",
                "Sí",
                "No",
                { limpiarCampos() },
                { },
                true
            )
        }

        binding.btnAgregarVital.setOnClickListener {
            showVitalDialog()
        }

        binding.btnAgregarMetabolico.setOnClickListener {
            showMetabolicoDialog()
        }

        binding.btnAgregarAntropometrico.setOnClickListener {
            showAntropometricoDialog()
        }

        // Signos Vitales
        configurarRemoverCampo(
            binding.btnRemoverTensionArterialSistolica,
            binding.layoutTensionArterialSistolica,
            binding.tfTensionArterialSistolica.editText,
            "Tensión Arterial Sistólica"
        )
        configurarRemoverCampo(
            binding.btnRemoverTensionArterialDiastolica,
            binding.layoutTensionArterialDiastolica,
            binding.tfTensionArterialDiastolica.editText,
            "Tensión Arterial Diastólica"
        )
        configurarRemoverCampo(
            binding.btnRemoverFrecuenciaCardiaca,
            binding.layoutFrecuenciaCardiaca,
            binding.tfFrecuenciaCardiaca.editText,
            "Frecuencia Cardíaca"
        )
        configurarRemoverCampo(
            binding.btnRemoverFrecuenciaRespiratoria,
            binding.layoutFrecuenciaRespiratoria,
            binding.tfFrecuenciaRespiratoria.editText,
            "Frecuencia Respiratoria"
        )
        configurarRemoverCampo(
            binding.btnRemoverTemperatura,
            binding.layoutTemperatura,
            binding.tfTemperatura.editText,
            "Temperatura"
        )
        configurarRemoverCampo(
            binding.btnRemoverSTO2,
            binding.layoutSTO2,
            binding.tfSTO2.editText,
            "Saturación de Oxígeno"
        )
        configurarRemoverCampo(
            binding.btnRemoverPulso,
            binding.layoutPulso,
            binding.tfPulso.editText,
            "Pulso"
        )
        // Signos metabolicos
        configurarRemoverCampo(
            binding.btnRemoverGlicemiaBasal,
            binding.layoutGlicemiaBasal,
            binding.tiGlicemiaBasal,
            "Glicemia Basal"
        )
        configurarRemoverCampo(
            binding.btnRemoverGlicemiaPostprandial,
            binding.layoutGlicemiaPostprandial,
            binding.tiGlicemiaPostprandial,
            "Glicemia Postprandial"
        )
        configurarRemoverCampo(
            binding.btnRemoverGlicemiaAleatoria,
            binding.layoutGlicemiaAleatoria,
            binding.tiGlicemiaAleatoria,
            "Glicemia Aleatoria"
        )
        configurarRemoverCampo(
            binding.btnRemoverHemoglobinaGlicosilada,
            binding.layoutHemoglobinaGlicosilada,
            binding.tiHemoglobinaGlicosilada,
            "Hemoglobina Glicosilada"
        )
        configurarRemoverCampo(
            binding.btnRemoverTrigliceridos,
            binding.layoutTrigliceridos,
            binding.tiTrigliceridos,
            "Triglicéridos"
        )
        configurarRemoverCampo(
            binding.btnRemoverColesterolTotal,
            binding.layoutColesterolTotal,
            binding.tiColesterolTotal,
            "Colesterol Total"
        )
        configurarRemoverCampo(
            binding.btnRemoverColesterolHdl,
            binding.layoutColesterolHdl,
            binding.tiColesterolHdl,
            "Colesterol HDL"
        )
        configurarRemoverCampo(
            binding.btnRemoverColesterolLdl,
            binding.layoutColesterolLdl,
            binding.tiColesterolLdl,
            "Colesterol LDL"
        )
        // Signos antropométricos
        configurarRemoverCampo(
            binding.btnRemoverPeso,
            binding.layoutPeso,
            binding.tfPeso.editText,
            "Peso"
        )
        configurarRemoverCampo(
            binding.btnRemoverAltura,
            binding.layoutAltura,
            binding.tfAltura.editText,
            "Altura (de pie)"
        )
        configurarRemoverCampo(
            binding.btnRemoverTalla,
            binding.layoutTalla,
            binding.tfTalla.editText,
            "Talla (acostado)"
        )
        configurarRemoverCampo(
            binding.btnRemoverCircuferenciaBraquial,
            binding.layoutCircuferenciaBraquial,
            binding.tfCircuferenciaBraquial.editText,
            "Circunferencia Braquial"
        )
        configurarRemoverCampo(
            binding.btnRemoverCircuferenciaCadera,
            binding.layoutCircuferenciaCadera,
            binding.tfCircuferenciaCadera.editText,
            "Circunferencia Cadera"
        )
        configurarRemoverCampo(
            binding.btnRemoverCircuferenciaCintura,
            binding.layoutCircuferenciaCintura,
            binding.tfCircuferenciaCintura.editText,
            "Circunferencia Cintura"
        )
        configurarRemoverCampo(
            binding.btnRemoverPerimetroCefalico,
            binding.layoutPerimetroCefalico,
            binding.tfPerimetroCefalico.editText,
            "Perímetro Cefálico"
        )
        configurarRemoverCampo(
            binding.btnRemoverPliegueTricipital,
            binding.layoutPliegueTricipital,
            binding.tfPliegueTricipital.editText,
            "Pliegue Tricipital"
        )
        configurarRemoverCampo(
            binding.btnRemoverPliegueSubescapular,
            binding.layoutPliegueSubescapular,
            binding.tfPliegueSubescapular.editText,
            "Pliegue Subescapular"
        )

    }

    private fun deshabilitarCamposConsulta() {
        // antropométricos
        binding.tfPeso.isEnabled = false
        binding.tfAltura.isEnabled = false
        binding.tfTalla.isEnabled = false
        binding.tfCircuferenciaBraquial.isEnabled = false
        binding.tfCircuferenciaCadera.isEnabled = false
        binding.tfCircuferenciaCintura.isEnabled = false
        binding.tfPerimetroCefalico.isEnabled = false
        binding.tfPliegueTricipital.isEnabled = false
        binding.tfPliegueSubescapular.isEnabled = false
        binding.btnAgregarAntropometrico.visibility = View.GONE
        // vitales
        binding.tfTensionArterialSistolica.isEnabled = false
        binding.tfTensionArterialDiastolica.isEnabled = false
        binding.tfFrecuenciaCardiaca.isEnabled = false
        binding.tfFrecuenciaRespiratoria.isEnabled = false
        binding.tfTemperatura.isEnabled = false
        binding.tfSTO2.isEnabled = false
        binding.tfPulso.isEnabled = false
        binding.btnAgregarVital.visibility = View.GONE
        // metabolicos
        binding.tfGlicemiaBasal.isEnabled = false
        binding.tfGlicemiaPostprandial.isEnabled = false
        binding.tfGlicemiaAleatoria.isEnabled = false
        binding.tfHemoglobinaGlicosilada.isEnabled = false
        binding.tfTrigliceridos.isEnabled = false
        binding.tfColesterolTotal.isEnabled = false
        binding.tfColesterolHdl.isEnabled = false
        binding.tfColesterolLdl.isEnabled = false
        binding.btnAgregarMetabolico.visibility = View.GONE
        // pediatrico
        binding.btnAgregarPediatrico.visibility = View.GONE
        // obstetricia
        binding.tfIsEmbarazo.isEnabled = false
        binding.tfFechaUltimaMenstruacion.isEnabled = false
        binding.tfSemanasGestacion.isEnabled = false
        binding.tfPesoPreEmbarazo.isEnabled = false

        binding.btnLimpiar.visibility = View.GONE
    }

    private fun showMetabolicoDialog() {
        val opciones = arrayOf(
            "Glicemia Basal", "Glicemia PostPrandial", "Glicemia Aleatoria",
            "Hemoglobina Glicosilada", "Triglicéridos", "Colesterol Total",
            "Colesterol HDL", "Colesterol LDL"
        )

        val layouts = listOf(
            binding.layoutGlicemiaBasal,
            binding.layoutGlicemiaPostprandial,
            binding.layoutGlicemiaAleatoria,
            binding.layoutHemoglobinaGlicosilada,
            binding.layoutTrigliceridos,
            binding.layoutColesterolTotal,
            binding.layoutColesterolHdl,
            binding.layoutColesterolLdl
        )

        val editTexts = listOf(
            binding.tiGlicemiaBasal,
            binding.tiGlicemiaPostprandial,
            binding.tiGlicemiaAleatoria,
            binding.tiHemoglobinaGlicosilada,
            binding.tiTrigliceridos,
            binding.tiColesterolTotal,
            binding.tiColesterolHdl,
            binding.tiColesterolLdl
        )

        val checksIniciales = layouts.map { it.isVisible }.toBooleanArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona los datos metabólicos")
            .setMultiChoiceItems(opciones, checksIniciales) { _, index, isChecked ->
                layouts[index].isVisible = isChecked
                if (!isChecked) {
                    editTexts[index].setText("")
                }
            }
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun showVitalDialog() {
        val opciones = arrayOf(
            "Tensión arterial sistólica",
            "Tensión arterial diastólica",
            "Frecuencia cardiaca",
            "Frecuencia respiratoria",
            "Temperatura",
            "Saturación de oxígeno",
            "Pulso"
        )

        val layouts = listOf(
            binding.layoutTensionArterialSistolica,
            binding.layoutTensionArterialDiastolica,
            binding.layoutFrecuenciaCardiaca,
            binding.layoutFrecuenciaRespiratoria,
            binding.layoutTemperatura,
            binding.layoutSTO2,
            binding.layoutPulso
        )

        val editTexts = listOf(
            binding.tfTensionArterialSistolica.editText,
            binding.tfTensionArterialDiastolica.editText,
            binding.tfFrecuenciaCardiaca.editText,
            binding.tfFrecuenciaRespiratoria.editText,
            binding.tfTemperatura.editText,
            binding.tfSTO2.editText,
            binding.tfPulso.editText
        )

        val checks = layouts.map { it.isVisible }.toBooleanArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona los signos vitales")
            .setMultiChoiceItems(opciones, checks) { _, index, isChecked ->
                layouts[index].isVisible = isChecked
                if (!isChecked) {
                    editTexts[index]?.setText("")
                }
            }
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun showAntropometricoDialog() {
        val opciones = arrayOf(
            "Peso",
            "Altura (De pie)",
            "Talla (Acostado)",
            "Circuferencia braquial",
            "Circuferencia cadera",
            "Circuferencia cintura",
            "Perímetro cefálico",
            "Pliegue tricipital",
            "Pliegue subescapular"
        )

        val layouts = listOf(
            binding.layoutPeso,
            binding.layoutAltura,
            binding.layoutTalla,
            binding.layoutCircuferenciaBraquial,
            binding.layoutCircuferenciaCadera,
            binding.layoutCircuferenciaCintura,
            binding.layoutPerimetroCefalico,
            binding.layoutPliegueTricipital,
            binding.layoutPliegueSubescapular
        )

        val editTexts = listOf(
            binding.tfPeso.editText,
            binding.tfAltura.editText,
            binding.tfTalla.editText,
            binding.tfCircuferenciaBraquial.editText,
            binding.tfCircuferenciaCadera.editText,
            binding.tfCircuferenciaCintura.editText,
            binding.tfPerimetroCefalico.editText,
            binding.tfPliegueTricipital.editText,
            binding.tfPliegueSubescapular.editText
        )

        val checks = layouts.map { it.isVisible }.toBooleanArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona los datos antropométricos")
            .setMultiChoiceItems(opciones, checks) { _, index, isChecked ->
                layouts[index].isVisible = isChecked
                if (!isChecked) {
                    editTexts[index]?.setText("")
                }
            }
            .setPositiveButton("Aceptar", null)
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

    private fun mostrarSelectorFecha(editText: TextInputEditText) {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        fun abrirPicker() {
            val fragmentManager = parentFragmentManager
            val existingPicker = fragmentManager.findFragmentByTag("MaterialDatePicker")
            if (existingPicker != null) {
                fragmentManager.beginTransaction().remove(existingPicker).commit()
            }

            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build()

            val seleccionInicial =
                ultimaFechaSeleccionada ?: MaterialDatePicker.todayInUtcMilliseconds()

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona la fecha")
                .setSelection(seleccionInicial)
                .setCalendarConstraints(constraints)
                .build()

            datePicker.addOnPositiveButtonClickListener { utcDateMillis ->
                ultimaFechaSeleccionada = utcDateMillis

                val localDate = Instant.ofEpochMilli(utcDateMillis)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()

                editText.setText(localDate.format(dateFormatter))
            }

            datePicker.show(fragmentManager, "MaterialDatePicker")
        }

        editText.setOnClickListener { abrirPicker() }
        binding.tfFechaUltimaMenstruacion.setStartIconOnClickListener { abrirPicker() }
    }

    private fun quitarErrores() {
        // Antropométricos
        binding.tfPeso.error = null
        binding.tfAltura.error = null
        binding.tfTalla.error = null
        binding.tfCircuferenciaBraquial.error = null
        binding.tfCircuferenciaCadera.error = null
        binding.tfCircuferenciaCintura.error = null
        binding.tfPerimetroCefalico.error = null
        binding.tfPliegueTricipital.error = null
        binding.tfPliegueSubescapular.error = null
        // Metabolico
        binding.tfGlicemiaBasal.error = null
        binding.tfGlicemiaPostprandial.error = null
        binding.tfGlicemiaAleatoria.error = null
        binding.tfHemoglobinaGlicosilada.error = null
        binding.tfTrigliceridos.error = null
        binding.tfColesterolTotal.error = null
        binding.tfColesterolHdl.error = null
        binding.tfColesterolLdl.error = null
        // Signos vitales
        binding.tfTensionArterialSistolica.error = null
        binding.tfTensionArterialDiastolica.error = null
        binding.tfFrecuenciaCardiaca.error = null
        binding.tfFrecuenciaRespiratoria.error = null
        binding.tfTemperatura.error = null
        binding.tfSTO2.error = null
        binding.tfPulso.error = null
        // Obstétrico
        binding.tfIsEmbarazo.error = null
        binding.tfFechaUltimaMenstruacion.error = null
        binding.tfSemanasGestacion.error = null
        binding.tfPesoPreEmbarazo.error = null
        // Pediatrico
    }

    private fun limpiarCampos() {
        quitarErrores()
        // Antropometrico
        binding.tfPeso.editText?.text = null
        binding.tfAltura.editText?.text = null
        binding.tfTalla.editText?.text = null
        binding.tfCircuferenciaBraquial.editText?.text = null
        binding.tfCircuferenciaCadera.editText?.text = null
        binding.tfCircuferenciaCintura.editText?.text = null
        binding.tfPerimetroCefalico.editText?.text = null
        binding.tfPliegueTricipital.editText?.text = null
        binding.tfPliegueSubescapular.editText?.text = null
        // Metabolico
        binding.tfGlicemiaBasal.editText?.text = null
        binding.tfGlicemiaPostprandial.editText?.text = null
        binding.tfGlicemiaAleatoria.editText?.text = null
        binding.tfHemoglobinaGlicosilada.editText?.text = null
        binding.tfTrigliceridos.editText?.text = null
        binding.tfColesterolTotal.editText?.text = null
        binding.tfColesterolHdl.editText?.text = null
        binding.tfColesterolLdl.editText?.text = null
        // Vitales
        binding.tfTensionArterialSistolica.editText?.text = null
        binding.tfTensionArterialDiastolica.editText?.text = null
        binding.tfFrecuenciaCardiaca.editText?.text = null
        binding.tfFrecuenciaRespiratoria.editText?.text = null
        binding.tfTemperatura.editText?.text = null
        binding.tfSTO2.editText?.text = null
        binding.tfPulso.editText?.text = null
        // Obstétrico
        binding.tfIsEmbarazo.editText?.text = null
        binding.tfFechaUltimaMenstruacion.editText?.text = null
        binding.tfSemanasGestacion.editText?.text = null
        binding.tfPesoPreEmbarazo.editText?.text = null
        // Pediatrico

    }

    private fun configurarRemoverCampo(
        boton: View,
        layout: View,
        campoTexto: EditText?,
        nombreCampo: String
    ) {
        boton.setOnClickListener {
            Utils.mostrarDialog(
                requireContext(),
                "Advertencia",
                "¿Desea remover el campo $nombreCampo?",
                "Sí",
                "No",
                {
                    layout.visibility = View.GONE
                    campoTexto?.text = null
                },
                {},
                true
            )
        }
    }

    private fun <T> AutoCompleteTextView.bind(
        lifecycleOwner: LifecycleOwner,
        itemsLive: LiveData<List<T>>,
        toText: (T) -> String,
        onItemSelected: (T) -> Unit
    ) {
        var currentItems: List<T> = emptyList()

        itemsLive.observe(lifecycleOwner) { items ->
            currentItems = items
            val names = items.map(toText)
            val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, names)
            setAdapter(adapter)
            if (text.toString() !in names) {
                setText("", false)
            }
        }

        setOnItemClickListener { _, _, position, _ ->
            onItemSelected(currentItems[position])
        }
    }

}