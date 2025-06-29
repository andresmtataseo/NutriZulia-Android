package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
//import com.nutrizulia.presentation.viewmodel.VerPacienteViewModel
import com.nutrizulia.databinding.FragmentVerPacienteBinding
import com.nutrizulia.presentation.viewmodel.VerPacienteViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerPacienteFragment : Fragment() {

    private val viewModel: VerPacienteViewModel by viewModels()
    private lateinit var binding: FragmentVerPacienteBinding
    private val args: VerPacienteFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVerPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.obtenerPaciente(args.idPaciente)

        viewModel.isLoading.observe(viewLifecycleOwner) { binding.progress.visibility = if (it) View.VISIBLE else View.INVISIBLE }

        viewModel.mensaje.observe(viewLifecycleOwner) { mostrarSnackbar(binding.root, it) }

        viewModel.salir.observe(viewLifecycleOwner) { requireActivity().onBackPressedDispatcher.onBackPressed() }

        viewModel.paciente.observe(viewLifecycleOwner) {
            binding.tfCedula.editText?.setText(it.cedula)
            binding.tfPrimerNombre.editText?.setText(it.nombres)
            binding.tfPrimerApellido.editText?.setText(it.apellidos)
            binding.tfFechaNacimiento.editText?.setText(it.fechaNacimiento.toString())
            binding.tfGenero.editText?.setText(it.genero)
            binding.tfTelefono.editText?.setText(it.telefono)
            binding.tfCorreo.editText?.setText(it.correo)
        }

        viewModel.etnia.observe(viewLifecycleOwner) { binding.tfEtnia.editText?.setText(it.nombre) }
        viewModel.nacionalidad.observe(viewLifecycleOwner) { binding.tfNacionalidad.editText?.setText(it.nombre) }
        viewModel.estado.observe(viewLifecycleOwner) { binding.tfEstado.editText?.setText(it.nombre) }
        viewModel.municipio.observe(viewLifecycleOwner) { binding.tfMunicipio.editText?.setText(it.nombre) }
        viewModel.parroquia.observe(viewLifecycleOwner) { binding.tfParroquia.editText?.setText(it.nombre) }

    }

}