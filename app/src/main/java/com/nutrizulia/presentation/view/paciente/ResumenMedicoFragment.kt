package com.nutrizulia.presentation.view.paciente

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nutrizulia.R
import com.nutrizulia.presentation.viewmodel.paciente.ResumenMedicoViewModel

class ResumenMedicoFragment : Fragment() {

    companion object {
        fun newInstance() = ResumenMedicoFragment()
    }

    private val viewModel: ResumenMedicoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_resumen_medico, container, false)
    }
}