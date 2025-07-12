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
import com.nutrizulia.util.ModoConsulta
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrarConsulta2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.cargarDatosClinicos()
        setupObservers()
        setupListeners()

    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
            binding.content.visibility = if (it) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) {
            Utils.mostrarSnackbar(binding.root, it)
        }

        viewModel.modoConsulta.observe(viewLifecycleOwner) { modo ->
            when (modo) {
                ModoConsulta.CREAR_SIN_CITA,
                ModoConsulta.CULMINAR_CITA,
                ModoConsulta.EDITAR_CONSULTA -> habilitarCampos()

                ModoConsulta.VER_CONSULTA -> {
                    deshabilitarCamposConsulta()
                    verificarYMostrarMensajesSinDatos()
                }
            }
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
            
            // Verificar mensajes si estamos en modo solo ver
            if (viewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                verificarYMostrarMensajesSinDatos()
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
            
            // Verificar mensajes si estamos en modo solo ver
            if (viewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                verificarYMostrarMensajesSinDatos()
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
            
            // Verificar mensajes si estamos en modo solo ver
            if (viewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                verificarYMostrarMensajesSinDatos()
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
            
            // Verificar mensajes si estamos en modo solo ver
            if (viewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                verificarYMostrarMensajesSinDatos()
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
            
            // Verificar mensajes si estamos en modo solo ver
            if (viewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                verificarYMostrarMensajesSinDatos()
            }
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

    private fun habilitarCampos() {
        // Mostrar botones de remover campo cuando es modo de edición
        mostrarBotonesRemoverCampo()
        
        // Ocultar mensajes de "sin datos" cuando se habilita la edición
        ocultarMensajesSinDatos()
    }

    private fun deshabilitarCamposConsulta() {
        // antropométricos
        binding.tiPeso.isEnabled = false
        binding.tiAltura.isEnabled = false
        binding.tiTalla.isEnabled = false
        binding.tiCircuferenciaBraquial.isEnabled = false
        binding.tiCircuferenciaCadera.isEnabled = false
        binding.tiCircuferenciaCintura.isEnabled = false
        binding.tiPerimetroCefalico.isEnabled = false
        binding.tiPliegueTricipital.isEnabled = false
        binding.tiPliegueSubescapular.isEnabled = false
        binding.btnAgregarAntropometrico.visibility = View.GONE
        // vitales
        binding.tiTensionArterialSistolica.isEnabled = false
        binding.tiTensionArterialDiastolica.isEnabled = false
        binding.tiFrecuenciaCardiaca.isEnabled = false
        binding.tiFrecuenciaRespiratoria.isEnabled = false
        binding.tiTemperatura.isEnabled = false
        binding.tiSTO2.isEnabled = false
        binding.tiPulso.isEnabled = false
        binding.btnAgregarVital.visibility = View.GONE
        // metabolicos
        binding.tiGlicemiaBasal.isEnabled = false
        binding.tiGlicemiaPostprandial.isEnabled = false
        binding.tiGlicemiaAleatoria.isEnabled = false
        binding.tiHemoglobinaGlicosilada.isEnabled = false
        binding.tiTrigliceridos.isEnabled = false
        binding.tiColesterolTotal.isEnabled = false
        binding.tiColesterolHdl.isEnabled = false
        binding.tiColesterolLdl.isEnabled = false
        binding.btnAgregarMetabolico.visibility = View.GONE
        // pediatrico
        binding.btnAgregarPediatrico.visibility = View.GONE
        // obstetricia
        binding.dropdownIsEmbarazo.isEnabled = false
        binding.tiFechaUltimaMenstruacion.isEnabled = false
        binding.tiSemanasGestacion.isEnabled = false
        binding.tiPesoPreEmbarazo.isEnabled = false

        binding.btnLimpiar.visibility = View.GONE
        
        // Ocultar todos los botones de remover campo cuando es modo solo ver
        ocultarBotonesRemoverCampo()
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

    /**
     * Oculta todos los botones de remover campo cuando el modo es VER_CONSULTA.
     * Esto evita que el usuario pueda eliminar campos en modo de solo lectura.
     */
    private fun ocultarBotonesRemoverCampo() {
        // Botones de remover signos vitales
        binding.btnRemoverTensionArterialSistolica.visibility = View.GONE
        binding.btnRemoverTensionArterialDiastolica.visibility = View.GONE
        binding.btnRemoverFrecuenciaCardiaca.visibility = View.GONE
        binding.btnRemoverFrecuenciaRespiratoria.visibility = View.GONE
        binding.btnRemoverTemperatura.visibility = View.GONE
        binding.btnRemoverSTO2.visibility = View.GONE
        binding.btnRemoverPulso.visibility = View.GONE
        
        // Botones de remover datos metabólicos
        binding.btnRemoverGlicemiaBasal.visibility = View.GONE
        binding.btnRemoverGlicemiaPostprandial.visibility = View.GONE
        binding.btnRemoverGlicemiaAleatoria.visibility = View.GONE
        binding.btnRemoverHemoglobinaGlicosilada.visibility = View.GONE
        binding.btnRemoverTrigliceridos.visibility = View.GONE
        binding.btnRemoverColesterolTotal.visibility = View.GONE
        binding.btnRemoverColesterolHdl.visibility = View.GONE
        binding.btnRemoverColesterolLdl.visibility = View.GONE
        
        // Botones de remover datos antropométricos
        binding.btnRemoverPeso.visibility = View.GONE
        binding.btnRemoverAltura.visibility = View.GONE
        binding.btnRemoverTalla.visibility = View.GONE
        binding.btnRemoverCircuferenciaBraquial.visibility = View.GONE
        binding.btnRemoverCircuferenciaCadera.visibility = View.GONE
        binding.btnRemoverCircuferenciaCintura.visibility = View.GONE
        binding.btnRemoverPerimetroCefalico.visibility = View.GONE
        binding.btnRemoverPliegueTricipital.visibility = View.GONE
        binding.btnRemoverPliegueSubescapular.visibility = View.GONE
    }

    /**
     * Verifica si los grupos de datos están vacíos y muestra mensajes informativos
     * cuando el modo es VER_CONSULTA y no hay datos disponibles.
     */
    private fun verificarYMostrarMensajesSinDatos() {
        // Verificar signos vitales
        val haySignosVitales = viewModel.detalleVital.value?.let { vital ->
            vital.tensionArterialSistolica != null ||
            vital.tensionArterialDiastolica != null ||
            vital.frecuenciaCardiaca != null ||
            vital.frecuenciaRespiratoria != null ||
            vital.temperatura != null ||
            vital.saturacionOxigeno != null ||
            vital.pulso != null
        } ?: false
        
        binding.tvSinDatosVitales.visibility = if (haySignosVitales) View.GONE else View.VISIBLE
        
        // Verificar datos metabólicos
        val hayDatosMetabolicos = viewModel.detalleMetabolico.value?.let { metabolico ->
            metabolico.glicemiaBasal != null ||
            metabolico.glicemiaPostprandial != null ||
            metabolico.glicemiaAleatoria != null ||
            metabolico.hemoglobinaGlicosilada != null ||
            metabolico.trigliceridos != null ||
            metabolico.colesterolTotal != null ||
            metabolico.colesterolHdl != null ||
            metabolico.colesterolLdl != null
        } ?: false
        
        binding.tvSinDatosMetabolicos.visibility = if (hayDatosMetabolicos) View.GONE else View.VISIBLE
        
        // Verificar datos antropométricos
        val hayDatosAntropometricos = viewModel.detalleAntropometrico.value?.let { antropometrico ->
            antropometrico.peso != null ||
            antropometrico.altura != null ||
            antropometrico.talla != null ||
            antropometrico.circunferenciaBraquial != null ||
            antropometrico.circunferenciaCadera != null ||
            antropometrico.circunferenciaCintura != null ||
            antropometrico.perimetroCefalico != null ||
            antropometrico.pliegueTricipital != null ||
            antropometrico.pliegueSubescapular != null
        } ?: false
        
        binding.tvSinDatosAntropometricos.visibility = if (hayDatosAntropometricos) View.GONE else View.VISIBLE
        
        // Verificar datos obstétricos
        val hayDatosObstetricos = viewModel.detalleObstetricia.value?.let { obstetricia ->
            obstetricia.estaEmbarazada != null ||
            obstetricia.fechaUltimaMenstruacion != null ||
            obstetricia.semanasGestacion != null ||
            obstetricia.pesoPreEmbarazo != null
        } ?: false
        
        binding.tvSinDatosObstetricos.visibility = if (hayDatosObstetricos) View.GONE else View.VISIBLE
        
        // Verificar datos pediátricos
        val hayDatosPediatricos = viewModel.detallePediatrico.value?.let { pediatrico ->
            pediatrico.usaBiberon != null ||
            pediatrico.tipoLactancia != null
        } ?: false
        
        binding.tvSinDatosPediatricos.visibility = if (hayDatosPediatricos) View.GONE else View.VISIBLE
    }

    /**
     * Oculta todos los mensajes de "sin datos" cuando se habilita la edición.
     */
    private fun ocultarMensajesSinDatos() {
        binding.tvSinDatosVitales.visibility = View.GONE
        binding.tvSinDatosMetabolicos.visibility = View.GONE
        binding.tvSinDatosAntropometricos.visibility = View.GONE
        binding.tvSinDatosObstetricos.visibility = View.GONE
        binding.tvSinDatosPediatricos.visibility = View.GONE
    }

    /**
     * Muestra todos los botones de remover campo cuando el modo permite edición.
     * Esto permite que el usuario pueda eliminar campos en modos de edición.
     */
    private fun mostrarBotonesRemoverCampo() {
        // Botones de remover signos vitales
        binding.btnRemoverTensionArterialSistolica.visibility = View.VISIBLE
        binding.btnRemoverTensionArterialDiastolica.visibility = View.VISIBLE
        binding.btnRemoverFrecuenciaCardiaca.visibility = View.VISIBLE
        binding.btnRemoverFrecuenciaRespiratoria.visibility = View.VISIBLE
        binding.btnRemoverTemperatura.visibility = View.VISIBLE
        binding.btnRemoverSTO2.visibility = View.VISIBLE
        binding.btnRemoverPulso.visibility = View.VISIBLE
        
        // Botones de remover datos metabólicos
        binding.btnRemoverGlicemiaBasal.visibility = View.VISIBLE
        binding.btnRemoverGlicemiaPostprandial.visibility = View.VISIBLE
        binding.btnRemoverGlicemiaAleatoria.visibility = View.VISIBLE
        binding.btnRemoverHemoglobinaGlicosilada.visibility = View.VISIBLE
        binding.btnRemoverTrigliceridos.visibility = View.VISIBLE
        binding.btnRemoverColesterolTotal.visibility = View.VISIBLE
        binding.btnRemoverColesterolHdl.visibility = View.VISIBLE
        binding.btnRemoverColesterolLdl.visibility = View.VISIBLE
        
        // Botones de remover datos antropométricos
        binding.btnRemoverPeso.visibility = View.VISIBLE
        binding.btnRemoverAltura.visibility = View.VISIBLE
        binding.btnRemoverTalla.visibility = View.VISIBLE
        binding.btnRemoverCircuferenciaBraquial.visibility = View.VISIBLE
        binding.btnRemoverCircuferenciaCadera.visibility = View.VISIBLE
        binding.btnRemoverCircuferenciaCintura.visibility = View.VISIBLE
        binding.btnRemoverPerimetroCefalico.visibility = View.VISIBLE
        binding.btnRemoverPliegueTricipital.visibility = View.VISIBLE
        binding.btnRemoverPliegueSubescapular.visibility = View.VISIBLE
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