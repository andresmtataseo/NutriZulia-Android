package com.nutrizulia.presentation.view.actividad

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nutrizulia.R
import com.nutrizulia.presentation.viewmodel.actividad.RegistrarActividadViewModel

class RegistrarActividadFragment : Fragment() {

    private val viewModel: RegistrarActividadViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_registrar_actividad, container, false)
    }
}