package com.nutrizulia.presentation.view.paciente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nutrizulia.R
import com.nutrizulia.presentation.viewmodel.HistoriaPacienteViewModel

class HistoriaPacienteFragment : Fragment() {

    companion object {
        fun newInstance() = HistoriaPacienteFragment()
    }

    private val viewModel: HistoriaPacienteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_historia_paciente, container, false)
    }
}