package com.nutrizulia.presentation.view.paciente

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.nutrizulia.databinding.FragmentSeleccionarRepresentanteBinding
import com.nutrizulia.presentation.adapter.SeleccionarRepresentanteAdapter
import com.nutrizulia.presentation.viewmodel.paciente.SeleccionarRepresentanteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeleccionarRepresentanteFragment : BottomSheetDialogFragment() {

    private val viewModel: SeleccionarRepresentanteViewModel by viewModels()
    private var _binding: FragmentSeleccionarRepresentanteBinding? = null
    private val binding: FragmentSeleccionarRepresentanteBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeleccionarRepresentanteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs(): Unit {
        // 1. Instanciar el adapter
        val pagerAdapter: SeleccionarRepresentanteAdapter = SeleccionarRepresentanteAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // 2. Conectar TabLayout con ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabTitle(position: Int): String {
        return when (position) {
            0 -> "Buscar"
            1 -> "Registrar"
            else -> ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}