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
import com.nutrizulia.databinding.FragmentVerCitaBinding
//import com.nutrizulia.presentation.viewmodel.VerCitaViewModel
import com.nutrizulia.util.Utils.calcularEdad
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerCitaFragment : Fragment() {

//    private val viewModel: VerCitaViewModel by viewModels()
    private lateinit var binding: FragmentVerCitaBinding
    private val args: VerCitaFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVerCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.obtenerPaciente(args.IdCita)
//
//        viewModel.citaConPaciente.observe(viewLifecycleOwner) {
//            binding.tfNombreCompletoPaciente.editText?.setText("${it.paciente.primerNombre} ${it.paciente.segundoNombre} ${it.paciente.primerApellido} ${it.paciente.segundoApellido}")
//            binding.tfEdadPaciente.editText?.setText("${calcularEdad(it.paciente.fechaNacimiento)}")
//            binding.tfGeneroPaciente.editText?.setText(it.paciente.genero)
//
//            binding.tfTipoCita.editText?.setText(it.cita.tipoCita)
//            binding.tfEspecialidad.editText?.setText(it.cita.especialidad)
//            binding.tfMotivoCita.editText?.setText(it.cita.motivoCita)
//            binding.tfFechaCita.editText?.setText(it.cita.fechaProgramada)
//            binding.tfHoraCita.editText?.setText(it.cita.horaProgramada)
//        }
//
//        viewModel.isLoading.observe(viewLifecycleOwner) { binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE }
//        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(requireView(), it) }
//        viewModel.salir.observe(viewLifecycleOwner) { if (it) findNavController().popBackStack() }

    }

}