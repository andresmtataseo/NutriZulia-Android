package com.nutrizulia.presentation.view.consulta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentRegistrarConsulta3Binding
import com.nutrizulia.presentation.viewmodel.RegistrarConsultaViewModel
import com.nutrizulia.util.ModoConsulta
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class RegistrarConsulta3Fragment : Fragment() {

    private val viewModel: RegistrarConsultaViewModel by navGraphViewModels(R.id.registrarConsultaGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var binding: FragmentRegistrarConsulta3Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarConsulta3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

                ModoConsulta.VER_CONSULTA -> deshabilitarCampos()
            }
        }

        viewModel.consulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta != null) {
                binding.tfObservaciones.editText?.setText(consulta.observaciones.orEmpty())
                binding.tfPlanes.editText?.setText(consulta.planes.orEmpty())
            }

            viewModel.salir.observe(viewLifecycleOwner) { salir ->
                if (salir) findNavController().popBackStack(R.id.consultasFragment, false)
            }

        }
    }

    private fun setupListeners() {

        binding.btnRegistrarConsulta.setOnClickListener {
            if (viewModel.modoConsulta.value == ModoConsulta.VER_CONSULTA) {
                findNavController().popBackStack(R.id.consultasFragment, false)
                return@setOnClickListener
            }

            viewModel.guardarConsultaCompleta(
                observaciones = binding.tfObservaciones.editText?.text.toString(),
                planes = binding.tfPlanes.editText?.text.toString()
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

    }

    private fun habilitarCampos() {
        binding.btnRegistrarConsulta.isEnabled = true
        binding.btnLimpiar.isEnabled = true
    }

    private fun deshabilitarCampos() {
        binding.tfObservaciones.editText?.isEnabled = false
        binding.tfPlanes.editText?.isEnabled = false
        binding.btnLimpiar.visibility = View.GONE
        binding.btnRegistrarConsulta.text = "Salir"
    }

    private fun quitarErrores() {

    }

    private fun limpiarCampos() {
        quitarErrores()
        binding.tfObservaciones.editText?.text = null
        binding.tfPlanes.editText?.text = null
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
            val adapter =
                ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, names)
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
