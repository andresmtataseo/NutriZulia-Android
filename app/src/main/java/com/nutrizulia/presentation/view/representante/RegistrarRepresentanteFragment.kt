package com.nutrizulia.presentation.view.representante

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.nutrizulia.databinding.FragmentRegistrarRepresentanteBinding
import com.nutrizulia.presentation.viewmodel.representante.RegistrarRepresentanteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrarRepresentanteFragment : Fragment() {

    private var _binding: FragmentRegistrarRepresentanteBinding? = null
    private val binding: FragmentRegistrarRepresentanteBinding get() = _binding!!
    private val viewModel: RegistrarRepresentanteViewModel by viewModels()
//    private val args: RegistrarRepresentanteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrarRepresentanteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()
//        setupListeners()
//        setupObservers()
    }

}