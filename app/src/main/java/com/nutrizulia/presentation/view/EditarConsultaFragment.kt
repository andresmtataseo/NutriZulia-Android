package com.nutrizulia.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nutrizulia.R
import com.nutrizulia.presentation.viewmodel.EditarConsultaViewModel

class EditarConsultaFragment : Fragment() {

    companion object {
        fun newInstance() = EditarConsultaFragment()
    }

    private val viewModel: EditarConsultaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_editar_consulta, container, false)
    }
}