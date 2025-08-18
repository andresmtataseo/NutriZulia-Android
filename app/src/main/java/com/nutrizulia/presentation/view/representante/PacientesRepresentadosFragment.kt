package com.nutrizulia.presentation.view.representante

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.nutrizulia.databinding.FragmentPacientesRepresentadosBinding
import com.nutrizulia.presentation.viewmodel.representante.PacientesRepresentadosViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PacientesRepresentadosFragment : Fragment() {

    private val viewModel: PacientesRepresentadosViewModel by viewModels()
    private lateinit var binding: FragmentPacientesRepresentadosBinding
    private val args: PacientesRepresentadosFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPacientesRepresentadosBinding.inflate(inflater, container, false)
        return binding.root
    }
}