package com.nutrizulia.presentation.view.paciente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.nutrizulia.databinding.FragmentHistoriaPacienteBinding
import com.nutrizulia.presentation.viewmodel.paciente.HistoriaPacienteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoriaPacienteFragment : Fragment() {

    private val viewModel: HistoriaPacienteViewModel by viewModels()
    private lateinit var binding: FragmentHistoriaPacienteBinding
    private val args: HistoriaPacienteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoriaPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}