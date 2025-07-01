package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrizulia.databinding.FragmentSeleccionarInstitucionBinding
import com.nutrizulia.presentation.adapter.InstitucionAdapter
import com.nutrizulia.presentation.viewmodel.SeleccionarInstitucionViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeleccionarInstitucionFragment : Fragment() {

    private val viewModel: SeleccionarInstitucionViewModel by viewModels()
    private lateinit var binding: FragmentSeleccionarInstitucionBinding
    private lateinit var institucionAdapter: InstitucionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeleccionarInstitucionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()
        setupRecyclerViews()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerViews() {
        institucionAdapter = InstitucionAdapter(emptyList()) { perfilSeleccionado ->
            viewModel.onInstitutionSelected(perfilSeleccionado.usuarioInstitucionId)
        }
        binding.recyclerViewInstituciones.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = institucionAdapter
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onCreate()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            if (mensaje.isNotEmpty()) {
                mostrarSnackbar(binding.root, mensaje)
            }
        }

        viewModel.continuar.observe(viewLifecycleOwner) { isReadyToContinue ->
            binding.btnAceptar.visibility = if (isReadyToContinue) View.VISIBLE else View.INVISIBLE
        }

        viewModel.perfilInstitucional.observe(viewLifecycleOwner) { perfiles ->
            institucionAdapter.updatePerfilesInstitucionales(perfiles)
        }

        binding.btnAceptar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}