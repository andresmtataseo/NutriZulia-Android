package com.nutrizulia.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nutrizulia.databinding.FragmentAyudaBinding
import com.nutrizulia.presentation.viewmodel.AyudaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AyudaFragment : Fragment() {

    private val viewModel: AyudaViewModel by viewModels()
    private lateinit var binding: FragmentAyudaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAyudaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDescargar.setOnClickListener {

        }

    }
}