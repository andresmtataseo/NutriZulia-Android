package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.nutrizulia.presentation.viewmodel.VerPacienteViewModel
import com.nutrizulia.databinding.FragmentVerPacienteBinding
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

        viewModel.getPacienteById(args.idPaciente)

        viewModel.isLoading.observe(viewLifecycleOwner) { binding.progress.visibility = if (it) View.VISIBLE else View.GONE }

        viewModel.paciente.observe(viewLifecycleOwner) {
            binding.tfCedula.editText?.setText(it.cedula)
            binding.tfPrimerNombre.editText?.setText(it.primerNombre)
            binding.tfSegundoNombre.editText?.setText(it.segundoNombre)
            binding.tfPrimerApellido.editText?.setText(it.primerApellido)
            binding.tfSegundoApellido.editText?.setText(it.segundoApellido)
            binding.tfFechaNacimiento.editText?.setText(it.fechaNacimiento)
            binding.tfGenero.editText?.setText(it.genero)
            binding.tfEtnia.editText?.setText(it.etnia)
            binding.tfNacionalidad.editText?.setText(it.nacionalidad)
            binding.tfTelefono.editText?.setText(it.telefono)
            binding.tfCorreo.editText?.setText(it.correo)
        }

        viewModel.entidad.observe(viewLifecycleOwner) { binding.tfEstado.editText?.setText(it.entidad) }
        viewModel.municipio.observe(viewLifecycleOwner) { binding.tfMunicipio.editText?.setText(it.municipio) }
        viewModel.parroquia.observe(viewLifecycleOwner) { binding.tfParroquia.editText?.setText(it.parroquia) }
        viewModel.comunidad.observe(viewLifecycleOwner) { binding.tfComunidad.editText?.setText(it.nombreComunidad) }
    }

}