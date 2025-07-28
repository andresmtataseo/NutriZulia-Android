package com.nutrizulia.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nutrizulia.presentation.view.representante.RegistrarRepresentanteFragment
import com.nutrizulia.presentation.view.representante.RepresentantesFragment

private const val NUM_TABS: Int = 2

class SeleccionarRepresentanteAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RepresentantesFragment()
            1 -> RegistrarRepresentanteFragment()
            else -> throw IllegalStateException("Invalid adapter position: $position")
        }
    }
}