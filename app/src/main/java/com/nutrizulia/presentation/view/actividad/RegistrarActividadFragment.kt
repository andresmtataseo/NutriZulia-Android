package com.nutrizulia.presentation.view.actividad

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nutrizulia.databinding.FragmentRegistrarActividadBinding
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.presentation.viewmodel.actividad.RegistrarActividadViewModel
import com.nutrizulia.util.Utils.mostrarDialog
import com.nutrizulia.util.Utils.mostrarErrorEnCampo
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.Utils.obtenerTexto
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.component1
import kotlin.collections.component2

@AndroidEntryPoint
class RegistrarActividadFragment : Fragment() {

    private val viewModel: RegistrarActividadViewModel by viewModels()
    private lateinit var binding: FragmentRegistrarActividadBinding
    private val args: RegistrarActividadFragmentArgs by navArgs()
    private var ultimaFechaSeleccionada: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarActividadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate(args.actividadId, args.isEditable)
        setupObservers()
        setupListeners()

        if (!args.isEditable) {
            deshabilitarCampos()
            binding.btnRegistrar.text = "Salir"
        } else {
            if (args.actividadId != null) {
                binding.btnLimpiar.text = "Restaurar"
            }
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            if (mensaje.isNotBlank()) mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack()
        }

        viewModel.errores.observe(viewLifecycleOwner) { errores ->
            quitarErrores()
            errores.forEach { (key, message) ->
                when (key) {
                    "tipoActividad" -> mostrarErrorEnCampo(binding.tfTipoActividad, message)
                    "fecha" -> mostrarErrorEnCampo(binding.tfFechaActividad, message)
                    "direccion" -> mostrarErrorEnCampo(binding.tfDireccion, message)
                    "descripcion" -> mostrarErrorEnCampo(binding.tfDescripcion, message)
                    "cantidadParticipantes" -> mostrarErrorEnCampo(binding.tfCantidadParticipantes, message)
                    "cantidadSesiones" -> mostrarErrorEnCampo(binding.tfCantidadSesiones, message)
                    "duracionMinutos" -> mostrarErrorEnCampo(binding.tfDuracionMinutos, message)
                    "temaPrincipal" -> mostrarErrorEnCampo(binding.tfTemaPrincipal, message)
                    "programaImplementados" -> mostrarErrorEnCampo(binding.tfProgramaImplementados, message)
                    "urlEvidencia" -> mostrarErrorEnCampo(binding.tfUrlEvidencia, message)
                }
            }
        }

        viewModel.actividad.observe(viewLifecycleOwner) { actividad ->
            binding.tfFechaActividad.editText?.setText(actividad.fecha.toString())
            binding.tfDireccion.editText?.setText(actividad.direccion)
            binding.tfDescripcion.editText?.setText(actividad.descripcionGeneral)
            binding.tfCantidadParticipantes.editText?.setText(actividad.cantidadParticipantes?.toString() ?: "")
            binding.tfCantidadSesiones.editText?.setText(actividad.cantidadSesiones?.toString() ?: "")
            binding.tfDuracionMinutos.editText?.setText(actividad.duracionMinutos?.toString() ?: "")
            binding.tfTemaPrincipal.editText?.setText(actividad.temaPrincipal)
            binding.tfProgramaImplementados.editText?.setText(actividad.programasImplementados)
            binding.tfUrlEvidencia.editText?.setText(actividad.urlEvidencia)
        }
        viewModel.tipoActividades.observe(viewLifecycleOwner) { updateAdapter(binding.dropdownTipoActividad, it.map(TipoActividad::nombre)) }
        viewModel.selectTipoActividad.observe(viewLifecycleOwner) { binding.dropdownTipoActividad.setText(it?.nombre, false) }
        viewModel.camposVisibles.observe(viewLifecycleOwner) { camposVisibles ->
            actualizarVisibilidadCampos(camposVisibles)
        }
    }

    private fun setupListeners() {
        mostrarSelectorFecha(binding.tfFechaActividad.editText as TextInputEditText)

        binding.btnRegistrar.setOnClickListener {
            if (args.isEditable) {
                viewModel.onSaveActividadClicked(
                    id = args.actividadId,
                    fechaStr = obtenerTexto(binding.tfFechaActividad),
                    direccion = obtenerTextoONull(binding.tfDireccion),
                    descripcionGeneral = obtenerTextoONull(binding.tfDescripcion),
                    cantidadParticipantes = obtenerTexto(binding.tfCantidadParticipantes).toIntOrNull(),
                    cantidadSesiones = obtenerTexto(binding.tfCantidadSesiones).toIntOrNull(),
                    duracionMinutos = obtenerTexto(binding.tfDuracionMinutos).toIntOrNull(),
                    temaPrincipal = obtenerTextoONull(binding.tfTemaPrincipal),
                    programasImplementados = obtenerTextoONull(binding.tfProgramaImplementados),
                    urlEvidencia = obtenerTextoONull(binding.tfUrlEvidencia)
                )
            } else {
                findNavController().popBackStack()
            }
        }

        binding.btnLimpiar.setOnClickListener {
            val dialogTitle = if (args.actividadId == null) "Limpiar campos" else "Restaurar cambios"
            val dialogMessage = if (args.actividadId == null) "¿Desea limpiar todos los campos?" else "¿Desea restaurar los cambios?"
            val positiveButton = if (args.actividadId == null) "Limpiar" else "Restaurar"
            mostrarDialog(requireContext(), dialogTitle, dialogMessage, positiveButton, "No", {
                limpiarCampos()
                if (args.actividadId != null) viewModel.onCreate(args.actividadId, true)
            }, {}, true)
        }

        binding.dropdownTipoActividad.setOnItemClickListener { _, _, position, _ ->
            viewModel.tipoActividades.value?.get(position)?.let { 
                // Solo limpiar campos si es una nueva actividad (no edición)
                if (args.actividadId == null) {
                    limpiarCamposNoVisibles()
                }
                viewModel.onActividadSelected(it) 
            }
        }
    }

    private fun deshabilitarCampos() {
        listOf(
            binding.dropdownTipoActividad
        ).forEach {
            it.isEnabled = false
            (it.parent.parent as? TextInputLayout)?.endIconMode = TextInputLayout.END_ICON_NONE
        }

        listOf(
            binding.tiFechaActividad,
            binding.tiDireccion,
            binding.tiDescripcion,
            binding.tiCantidadParticipantes,
            binding.tiCantidadSesiones,
            binding.tiDuracionMinutos,
            binding.tiTemaPrincipal,
            binding.tiProgramaImplementados,
            binding.tiUrlEvidencia
        ).forEach {
            it.isEnabled = false
        }

        binding.btnLimpiar.visibility = View.GONE
    }

    private fun updateAdapter(dropdown: AutoCompleteTextView, items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        dropdown.setAdapter(adapter)
    }

    private fun limpiarCampos() {
        quitarErrores()
        binding.tfTipoActividad.editText?.text = null
        binding.tfFechaActividad.editText?.text = null
        binding.tfDescripcion.editText?.text = null
        binding.tfDireccion.editText?.text = null
        binding.tfCantidadParticipantes.editText?.text = null
        binding.tfCantidadSesiones.editText?.text = null
        binding.tfDuracionMinutos.editText?.text = null
        binding.tfTemaPrincipal.editText?.text = null
        binding.tfProgramaImplementados.editText?.text = null
        binding.tfUrlEvidencia.editText?.text = null
        binding.dropdownTipoActividad.setText("", false)
    }

    private fun quitarErrores() {
        binding.tfTipoActividad.error = null
        binding.tfFechaActividad.error = null
        binding.tfDescripcion.error = null
        binding.tfDireccion.error = null
        binding.tfCantidadParticipantes.error = null
        binding.tfCantidadSesiones.error = null
        binding.tfDuracionMinutos.error = null
        binding.tfTemaPrincipal.error = null
        binding.tfProgramaImplementados.error = null
        binding.tfUrlEvidencia.error = null
        binding.dropdownTipoActividad.error = null
    }

    private fun actualizarVisibilidadCampos(camposVisibles: Set<String>) {
        // Mostrar el layout de información detallada solo si hay campos visibles
        binding.layoutInformacionDetallada.visibility = if (camposVisibles.isNotEmpty()) View.VISIBLE else View.GONE
        
        // Mapeo de campos a sus respectivos TextInputLayouts
        val camposMapeados = mapOf(
            "fecha" to binding.tfFechaActividad,
            "direccion" to binding.tfDireccion,
            "descripcion" to binding.tfDescripcion,
            "cantidadParticipantes" to binding.tfCantidadParticipantes,
            "cantidadSesiones" to binding.tfCantidadSesiones,
            "duracionMinutos" to binding.tfDuracionMinutos,
            "temaPrincipal" to binding.tfTemaPrincipal,
            "programaImplementados" to binding.tfProgramaImplementados,
            "urlEvidencia" to binding.tfUrlEvidencia
        )
        
        // Actualizar visibilidad de cada campo individual
        camposMapeados.forEach { (campo, textInputLayout) ->
            textInputLayout.visibility = if (camposVisibles.contains(campo)) View.VISIBLE else View.GONE
        }
    }
    
    private fun limpiarCamposNoVisibles() {
        // Limpiar todos los campos antes de cambiar la visibilidad
        binding.tfFechaActividad.editText?.text = null
        binding.tfDireccion.editText?.text = null
        binding.tfDescripcion.editText?.text = null
        binding.tfCantidadParticipantes.editText?.text = null
        binding.tfCantidadSesiones.editText?.text = null
        binding.tfDuracionMinutos.editText?.text = null
        binding.tfTemaPrincipal.editText?.text = null
        binding.tfProgramaImplementados.editText?.text = null
        binding.tfUrlEvidencia.editText?.text = null
        quitarErrores()
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
        binding.tfFechaActividad.setStartIconOnClickListener { abrirPicker() }
    }

    /**
     * Función auxiliar que convierte strings vacíos o en blanco a null
     */
    private fun obtenerTextoONull(textInputLayout: TextInputLayout): String? {
        val texto = obtenerTexto(textInputLayout)
        return if (texto.isBlank()) null else texto
    }

}