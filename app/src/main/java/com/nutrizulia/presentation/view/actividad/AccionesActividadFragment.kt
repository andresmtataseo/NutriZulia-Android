package com.nutrizulia.presentation.view.actividad

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nutrizulia.databinding.FragmentAccionesActividadBinding
import com.nutrizulia.presentation.viewmodel.actividad.AccionesActividadViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccionesActividadFragment : Fragment() {

    private val viewModel: AccionesActividadViewModel by viewModels()
    private lateinit var binding: FragmentAccionesActividadBinding
    private val args: AccionesActividadFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccionesActividadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.onCreate(args.actividadId)
        setupListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) {
            mostrarSnackbar(requireView(), it)
        }

        viewModel.salir.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }

        viewModel.actividad.observe(viewLifecycleOwner) { actividad ->
            binding.tvNombreTipoActividad.text = actividad.nombreActividad
            binding.tvFechaActividad.text = "Fecha: ${actividad.fechaActividad}"
            binding.tvDescripcionGeneralActividad.text = "Descripción: ${actividad.descripcionGeneralActividad}"
        }
    }

    private fun setupListeners() {
        binding.cardViewInformacionPersonal.setOnClickListener {
            findNavController().navigate(
                AccionesActividadFragmentDirections.actionAccionesActividadFragmentToRegistrarActividadFragment(
                    args.actividadId, false
                )
            )
        }

        binding.cardViewEditarInformacionPersonal.setOnClickListener {
            findNavController().navigate(
                AccionesActividadFragmentDirections.actionAccionesActividadFragmentToRegistrarActividadFragment(
                    args.actividadId, true
                )
            )
        }

    }

}