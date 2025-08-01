package com.nutrizulia.presentation.view.representante

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nutrizulia.R
import com.nutrizulia.presentation.viewmodel.representante.AccionesRepresentanteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccionesRepresentanteFragment : Fragment() {

    companion object {
        fun newInstance() = AccionesRepresentanteFragment()
    }

    private val viewModel: AccionesRepresentanteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_acciones_representante, container, false)
    }
}